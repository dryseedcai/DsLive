package com.dryseed.dslive.hostlive;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.dryseed.dslive.DsApplication;
import com.dryseed.dslive.R;
import com.dryseed.dslive.model.ChatMsgInfo;
import com.dryseed.dslive.model.Constants;
import com.dryseed.dslive.model.GiftCmdInfo;
import com.dryseed.dslive.model.GiftInfo;
import com.dryseed.dslive.view.BottomControlView;
import com.dryseed.dslive.view.ChatMsgListView;
import com.dryseed.dslive.view.ChatView;
import com.dryseed.dslive.view.DanmuView;
import com.dryseed.dslive.view.GiftFullView;
import com.dryseed.dslive.view.GiftRepeatView;
import com.dryseed.dslive.widget.GiftSelectDialog;
import com.dryseed.dslive.widget.SizeChangeRelativeLayout;
import com.google.gson.Gson;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyrantgit.widget.HeartLayout;

/**
 * Created by caiminming on 2017/11/8.
 */
public class HostLiveActivity extends AppCompatActivity {

    @BindView(R.id.live_view)
    AVRootView mLiveView;

    @BindView(R.id.size_change_layout)
    SizeChangeRelativeLayout mSizeChangeRelativeLayout;

    @BindView(R.id.control_view)
    BottomControlView mControlView;

    @BindView(R.id.chat_view)
    ChatView mChatView;

    @BindView(R.id.chat_list)
    ChatMsgListView mChatListView;

    @BindView(R.id.danmu_view)
    DanmuView mDanmuView;

    @BindView(R.id.gift_repeat_view)
    GiftRepeatView mGiftRepeatView;

    @BindView(R.id.gift_full_view)
    GiftFullView mGiftFullView;

    @BindView(R.id.heart_layout)
    HeartLayout mHeartLayout;

    private int mRoomId;
    private GiftSelectDialog giftSelectDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_live);
        ButterKnife.bind(this);

        initView();
        createLive();
    }

    private void initView() {
        ILVLiveManager.getInstance().setAvVideoView(mLiveView);

        mSizeChangeRelativeLayout = (SizeChangeRelativeLayout) findViewById(R.id.size_change_layout);
        mSizeChangeRelativeLayout.setOnSizeChangeListener(new SizeChangeRelativeLayout.OnSizeChangeListener() {
            @Override
            public void onLarge() {
                //键盘隐藏
                mChatView.setVisibility(View.INVISIBLE);
                mControlView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSmall() {
                //键盘显示
            }
        });

        mControlView.setIsHost(true);
        mControlView.setOnControlListener(new BottomControlView.OnControlListener() {
            @Override
            public void onChatClick() {
                //点击了聊天按钮，显示聊天操作栏
                mChatView.setVisibility(View.VISIBLE);
                mControlView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCloseClick() {
                //点击了关闭按钮，关闭直播
                quitLive();
            }

            @Override
            public void onGiftClick() {
                //发送礼物
                //显示礼物九宫格
                if (giftSelectDialog == null) {
                    giftSelectDialog = new GiftSelectDialog(HostLiveActivity.this);

                    giftSelectDialog.setGiftSendListener(new GiftSelectDialog.OnGiftSendListener() {
                        @Override
                        public void onGiftSendClick(final ILVCustomCmd customCmd) {
                            customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());

                            ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
                                @Override
                                public void onSuccess(TIMMessage data) {
                                    if (customCmd.getCmd() == Constants.CMD_CHAT_GIFT) {
                                        //界面显示礼物动画。
                                        GiftCmdInfo giftCmdInfo = new Gson().fromJson(customCmd.getParam(), GiftCmdInfo.class);
                                        int giftId = giftCmdInfo.giftId;
                                        String repeatId = giftCmdInfo.repeatId;
                                        GiftInfo giftInfo = GiftInfo.getGiftById(giftId);
                                        if (giftInfo == null) {
                                            return;
                                        }
                                        if (giftInfo.type == GiftInfo.Type.ContinueGift) {
                                            mGiftRepeatView.showGift(giftInfo, repeatId, DsApplication.getApplication().getSelfProfile());
                                        } else if (giftInfo.type == GiftInfo.Type.FullScreenGift) {
                                            //全屏礼物
                                            mGiftFullView.showGift(giftInfo, DsApplication.getApplication().getSelfProfile());
                                        }
                                    }
                                }

                                @Override
                                public void onError(String module, int errCode, String errMsg) {
                                }

                            });
                        }
                    });
                }
                giftSelectDialog.show();
            }

            @Override
            public void onOptionClick(View view) {
                //显示主播操作对话框

//                boolean beautyOn = hostControlState.isBeautyOn();
//                boolean flashOn = flashlightHelper.isFlashLightOn();
//                boolean voiceOn = hostControlState.isVoiceOn();
//
//                HostControlDialog hostControlDialog = new HostControlDialog(HostLiveActivity.this);
//
//                hostControlDialog.setOnControlClickListener(controlClickListener);
//                hostControlDialog.updateView(beautyOn, flashOn, voiceOn);
//                hostControlDialog.show(view);
            }
        });

        mChatView.setOnChatSendListener(new ChatView.OnChatSendListener() {
            @Override
            public void onChatSend(final ILVCustomCmd customCmd) {
                //发送消息
                /*ILVText ilvText = new ILVText(ILVText.ILVTextType.eGroupMsg, "", customCmd.getParam());
                ILVLiveManager.getInstance().sendText(ilvText, new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        Toast.makeText(HostLiveActivity.this, "发送消息成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        Toast.makeText(HostLiveActivity.this, "发送消息失败", Toast.LENGTH_SHORT).show();
                    }
                });*/

                customCmd.setDestId(ILiveRoomManager.getInstance().getIMGroupId());
                ILVLiveManager.getInstance().sendCustomCmd(customCmd, new ILiveCallBack<TIMMessage>() {
                    @Override
                    public void onSuccess(TIMMessage data) {
                        if (customCmd.getCmd() == Constants.CMD_CHAT_MSG_LIST) {
                            //如果是列表类型的消息，发送给列表显示
                            String chatContent = customCmd.getParam();
                            String userId = DsApplication.getApplication().getSelfProfile().getIdentifier();
                            String avatar = DsApplication.getApplication().getSelfProfile().getFaceUrl();
                            ChatMsgInfo info = ChatMsgInfo.createListInfo(chatContent, userId, avatar);
                            mChatListView.addMsgInfo(info);
                        } else if (customCmd.getCmd() == Constants.CMD_CHAT_MSG_DANMU) {
                            String chatContent = customCmd.getParam();
                            String userId = DsApplication.getApplication().getSelfProfile().getIdentifier();
                            String avatar = DsApplication.getApplication().getSelfProfile().getFaceUrl();
                            ChatMsgInfo info = ChatMsgInfo.createListInfo(chatContent, userId, avatar);
                            mChatListView.addMsgInfo(info);

                            String name = DsApplication.getApplication().getSelfProfile().getNickName();
                            if (TextUtils.isEmpty(name)) {
                                name = userId;
                            }
                            ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(chatContent, userId, avatar, name);
                            mDanmuView.addMsgInfo(danmuInfo);
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                    }

                });
            }
        });

        mControlView.setVisibility(View.VISIBLE);
        mChatView.setVisibility(View.INVISIBLE);
    }

    private void createLive() {
        mRoomId = getIntent().getIntExtra("roomId", -1);
        if (mRoomId < 0) {
            Toast.makeText(this, "roomId is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        //配置直播Config
        //注意：腾讯云的群发消息，发送者自己是不会接收到消息的
        ILVLiveConfig liveConfig = DsApplication.getApplication().getLiveConfig();
        liveConfig.setLiveMsgListener(new ILVLiveConfig.ILVLiveMsgListener() {
            @Override
            public void onNewTextMsg(ILVText text, String SenderId, TIMUserProfile userProfile) {
                //接收到文本消息
                Toast.makeText(HostLiveActivity.this, "onNewTextMsg:" + text.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNewCustomMsg(ILVCustomCmd cmd, String id, TIMUserProfile userProfile) {
                Toast.makeText(HostLiveActivity.this, "onNewCustomMsg:" + cmd.getParam(), Toast.LENGTH_SHORT).show();
                //接收到自定义消息
                if (cmd.getCmd() == Constants.CMD_CHAT_MSG_LIST) {
                    String content = cmd.getParam();
                    ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, userProfile.getFaceUrl());
                    mChatListView.addMsgInfo(info);
                } else if (cmd.getCmd() == Constants.CMD_CHAT_MSG_DANMU) {
                    String content = cmd.getParam();
                    ChatMsgInfo info = ChatMsgInfo.createListInfo(content, id, userProfile.getFaceUrl());
                    mChatListView.addMsgInfo(info);

                    String name = userProfile.getNickName();
                    if (TextUtils.isEmpty(name)) {
                        name = userProfile.getIdentifier();
                    }
                    ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(content, id, userProfile.getFaceUrl(), name);
                    mDanmuView.addMsgInfo(danmuInfo);
                } else if (cmd.getCmd() == Constants.CMD_CHAT_GIFT) {
                    //界面显示礼物动画。
                    GiftCmdInfo giftCmdInfo = new Gson().fromJson(cmd.getParam(), GiftCmdInfo.class);
                    int giftId = giftCmdInfo.giftId;
                    String repeatId = giftCmdInfo.repeatId;
                    GiftInfo giftInfo = GiftInfo.getGiftById(giftId);
                    if (giftInfo == null) {
                        return;
                    }
                    if (giftInfo.type == GiftInfo.Type.ContinueGift) {
                        mGiftRepeatView.showGift(giftInfo, repeatId, userProfile);
                    } else if (giftInfo.type == GiftInfo.Type.FullScreenGift) {
                        //全屏礼物
                        mGiftFullView.showGift(giftInfo, userProfile);
                    }
                } /*else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_ENTER) {
                    //用户进入直播
                    mTitleView.addWatcher(userProfile);
                    mVipEnterView.showVipEnter(userProfile);
                } else if (cmd.getCmd() == ILVLiveConstants.ILVLIVE_CMD_LEAVE) {
                    //用户离开消息
                    mTitleView.removeWatcher(userProfile);
                }*/
            }

            @Override
            public void onNewOtherMsg(TIMMessage message) {
                Toast.makeText(HostLiveActivity.this, "onNewOtherMsg:" + message.toString(), Toast.LENGTH_SHORT).show();
                //接收到其他消息
            }
        });

        //配置直播间config
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(ILiveLoginManager.getInstance().getMyUserId()).
                controlRole("LiveMaster")//角色设置
                .autoFocus(true)
                .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)//权限设置
                .cameraId(ILiveConstants.BACK_CAMERA)//摄像头前置后置
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);//是否开始半自动接收


        //创建房间
        ILVLiveManager.getInstance().createRoom(mRoomId, hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Toast.makeText(HostLiveActivity.this, "创建直播成功！", Toast.LENGTH_SHORT).show();
                test(); //开启测试
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                //失败的情况下，退出界面。
                Toast.makeText(HostLiveActivity.this, "创建直播失败！", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
        if(null != mHeartTimer){
            mHeartTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        quitLive();
    }

    private void quitLive() {
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Toast.makeText(HostLiveActivity.this, "退出直播成功！", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(HostLiveActivity.this, "退出直播失败！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private Timer mHeartTimer = new Timer();
    private Random mHeartRandom = new Random();

    private void startHeartAnim() {
        mHeartTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.addHeart(getRandomColor());
                    }
                });
            }
        }, 0, 1000); //1秒钟
    }

    private int getRandomColor() {
        return Color.rgb(mHeartRandom.nextInt(255), mHeartRandom.nextInt(255), mHeartRandom.nextInt(255));
    }

    /**
     * 测试代码
     */
    private void test() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 7; i++) {
                    ChatMsgInfo danmuInfo = ChatMsgInfo.createDanmuInfo(
                            "danmu" + i,
                            i + "",
                            "http://att3.citysbs.com/no/chongqing/2017/11/08/13/193x160-133151_v2_16961510119111859_5630624050ee7e452afb4a72bae7dff8.png",
                            i + "");
                    mDanmuView.addMsgInfo(danmuInfo);
                }
            }
        }, 2000);

        startHeartAnim();
    }
}
