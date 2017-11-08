package com.dryseed.dslive.watcher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.dryseed.dslive.DsApplication;
import com.dryseed.dslive.R;
import com.dryseed.dslive.hostlive.HostLiveActivity;
import com.dryseed.dslive.model.ChatMsgInfo;
import com.dryseed.dslive.model.Constants;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by caiminming on 2017/11/8.
 */

public class WatcherActivity extends AppCompatActivity {

    @BindView(R.id.live_view)
    AVRootView mLiveView;

    private int mRoomId;
    private String mHostId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watcher);
        ButterKnife.bind(this);

        ILVLiveManager.getInstance().setAvVideoView(mLiveView);
        joinLive();
    }

    private void joinLive() {
        mRoomId = getIntent().getIntExtra("roomId", -1);
        mHostId = getIntent().getStringExtra("hostId");
        if (mRoomId < 0 || TextUtils.isEmpty(mHostId)) {
            Toast.makeText(this, "roomId or hostId is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        //配置直播Config
        ILVLiveConfig liveConfig = DsApplication.getApplication().getLiveConfig();
        liveConfig.setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
            @Override
            public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {
                //接收到文本消息
                Toast.makeText(WatcherActivity.this, "onNewTextMsg:" + text.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {
                Toast.makeText(WatcherActivity.this, "onNewCustomMsg:" + cmd.getParam(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNewOtherMsg(TIMMessage message) {
                //接收到其他消息
                Toast.makeText(WatcherActivity.this, "onNewOtherMsg:" + message.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //创建房间配置项
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(mHostId)
                .controlRole("Guest")//角色设置
                .autoCamera(false) //是否自动打开摄像头
                .autoFocus(true)
                .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                .cameraId(ILiveConstants.FRONT_CAMERA)//摄像头前置后置
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收

        //加入房间
        ILVLiveManager.getInstance().joinRoom(mRoomId, hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Toast.makeText(WatcherActivity.this, "加入直播成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //失败的情况下，退出界面。
                Toast.makeText(WatcherActivity.this, "加入直播失败！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ILVLiveManager.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ILVLiveManager.getInstance().onResume();
    }

    @Override
    public void onBackPressed() {
        quitLive();
        finish();
    }

    private void quitLive() {
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Toast.makeText(WatcherActivity.this, "退出直播成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(WatcherActivity.this, "退出直播失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
