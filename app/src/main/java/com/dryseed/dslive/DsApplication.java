package com.dryseed.dslive;

import android.app.Application;
import android.content.Context;

import com.dryseed.dslive.editprofile.CustomProfile;
import com.dryseed.dslive.utils.QnUploadHelper;
import com.tencent.TIMManager;
import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caiminming on 2017/11/7.
 */

public class DsApplication extends Application {

    private static DsApplication app;
    private static Context appContext;
    private TIMUserProfile mSelfProfile;

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;
        appContext = getApplicationContext();
        initLiveSdk();
    }

    private void initLiveSdk() {
        //初始化腾讯互动直播 核心功能
        ILiveSDK.getInstance().initSdk(this, 1400048248, 18913); //cloud.tencent.com

        //添加用户字段 需提交工单：2.3 新增用户资料维度的自定义字段
        // https://cloud.tencent.com/document/product/269/3916#2.3-.E6.96.B0.E5.A2.9E.E7.94.A8.E6.88.B7.E7.BB.B4.E5.BA.A6.E7.9A.84.E8.87.AA.E5.AE.9A.E4.B9.89.E5.AD.97.E6.AE.B5
        /*List<String> customInfos = new ArrayList<String>();
        customInfos.add(CustomProfile.CUSTOM_GET);
        customInfos.add(CustomProfile.CUSTOM_SEND);
        customInfos.add(CustomProfile.CUSTOM_LEVEL);
        customInfos.add(CustomProfile.CUSTOM_RENZHENG);
        TIMManager.getInstance().initFriendshipSettings(CustomProfile.allBaseInfo, customInfos);*/

        //初始化腾讯互动直播 直播SDK
        ILVLiveManager.getInstance().init(new ILVLiveConfig());

        //七牛云
        QnUploadHelper.init(
                "fywLTKHt3JUahQrTPSFrKRt27FjWTBV6Yn8CQFWe",
                "00nzSVpO5yURyMxpPkOP_9shEtnGYDbGJxMavzdL",
                "http://oe0i3jf0i.bkt.clouddn.com/",
                "imooc"
        );
    }

    public static Context getContext() {
        return appContext;
    }

    public static DsApplication getApplication() {
        return app;
    }

    public void setSelfProfile(TIMUserProfile userProfile) {
        mSelfProfile = userProfile;
    }

    public TIMUserProfile getSelfProfile() {
        return mSelfProfile;
    }
}
