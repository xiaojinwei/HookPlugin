package com.cj.jplugin.hook;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 变更：
 * Handle处理的生命周期使用了LaunchActivityItem extend ClientTransactionItem等 TRANSACTION
 */
public class HookStartActivityApi28 extends HookStartActivityApi26 {
    public static final int EXECUTE_TRANSACTION = 159;

    public HookStartActivityApi28(Context context, Class<?> proxyClass) {
        super(context, proxyClass);
    }

    @Override
    public void hookLaunchActivity() {
        super.hookLaunchActivity();
    }

    @Override
    protected boolean handleCallbackMessage(Message msg) {
        if (msg.what == EXECUTE_TRANSACTION){
            return handleLaunchActivity28(msg);
        }
        return false;
    }

    private boolean handleLaunchActivity28(Message msg) {
        boolean result = false;//返回值，请看Handler的源码，dispatchMessage就会懂了
        //Handler的dispatchMessage有3个callback优先级，首先是msg自带的callback，其次是Handler的成员mCallback,最后才是Handler类自身的handlerMessage方法,
        //它成员mCallback.handleMessage的返回值为true，则不会继续往下执行 Handler.handlerMessage
        //我们这里只是要hook，插入逻辑，所以必须返回false，让Handler原本的handlerMessage能够执行.
        if (msg.what == EXECUTE_TRANSACTION) {//这是跳转的时候,要对intent进行还原
            try {
                //先把相关@hide的类都建好
                Class<?> ClientTransactionClz = Class.forName("android.app.servertransaction.ClientTransaction");
                Class<?> LaunchActivityItemClz = Class.forName("android.app.servertransaction.LaunchActivityItem");

                Field mActivityCallbacksField = ClientTransactionClz.getDeclaredField("mActivityCallbacks");//ClientTransaction的成员
                mActivityCallbacksField.setAccessible(true);
                //类型判定，好习惯
                if (!ClientTransactionClz.isInstance(msg.obj)) return true;
                Object mActivityCallbacksObj = mActivityCallbacksField.get(msg.obj);//根据源码，在这个分支里面,msg.obj就是 ClientTransaction类型,所以，直接用
                //拿到了ClientTransaction的List<ClientTransactionItem> mActivityCallbacks;
                List list = (List) mActivityCallbacksObj;

                if (list.size() == 0) return true;
                Object LaunchActivityItemObj = list.get(0);//所以这里直接就拿到第一个就好了

                if (!LaunchActivityItemClz.isInstance(LaunchActivityItemObj)) return true;
                //这里必须判定 LaunchActivityItemClz，
                // 因为 最初的ActivityResultItem传进去之后都被转化成了这LaunchActivityItemClz的实例

                Field mIntentField = LaunchActivityItemClz.getDeclaredField("mIntent");
                mIntentField.setAccessible(true);
                Intent mIntent = (Intent) mIntentField.get(LaunchActivityItemObj);
                if(mIntent.hasExtra(EXTRA_ORIGIN_INTENT)) {
                    Intent oriIntent = (Intent) mIntent.getExtras().getParcelable(EXTRA_ORIGIN_INTENT);
                    //将占坑intent替换成插件中的intent
                    mIntentField.set(LaunchActivityItemObj, oriIntent);
                    //解决AppCompatActivity重新检测清单文件注册问题
                    handleAppCompatActivityCheck(mIntent);
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
