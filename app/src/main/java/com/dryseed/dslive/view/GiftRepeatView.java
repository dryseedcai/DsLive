package com.dryseed.dslive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.dryseed.dslive.R;
import com.dryseed.dslive.model.GiftInfo;
import com.tencent.TIMUserProfile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by caiminming on 2017/11/9.
 */
public class GiftRepeatView extends LinearLayout {

    private GiftRepeatItemView item0, item1;

    private class GiftSenderAndInfo {
        public GiftInfo giftInfo;
        public String repeatId;
        public TIMUserProfile senderProfile;
    }

    private List<GiftSenderAndInfo> giftSenderAndInfoList = new LinkedList<GiftSenderAndInfo>();

    public GiftRepeatView(Context context) {
        super(context);
        init();
    }

    public GiftRepeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftRepeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_gift_repeat, this, true);
        findAllViews();
        item0.setVisibility(INVISIBLE);
        item1.setVisibility(INVISIBLE);
    }

    private void findAllViews() {
        item0 = (GiftRepeatItemView) findViewById(R.id.item0);
        item1 = (GiftRepeatItemView) findViewById(R.id.item1);

        item0.setOnGiftItemAvaliableListener(avaliableListener);
        item1.setOnGiftItemAvaliableListener(avaliableListener);
    }

    private GiftRepeatItemView.OnGiftItemAvaliableListener avaliableListener = new GiftRepeatItemView.OnGiftItemAvaliableListener() {
        @Override
        public void onAvaliable() {
            if (giftSenderAndInfoList.size() > 0) {
                GiftSenderAndInfo giftSenderAndInfo = giftSenderAndInfoList.remove(0);
                showGift(giftSenderAndInfo.giftInfo, giftSenderAndInfo.repeatId, giftSenderAndInfo.senderProfile);
                //找出缓存中和第一个礼物相同的连发礼物
                List<GiftSenderAndInfo> giftSameInfos = new ArrayList<>();
                for (GiftSenderAndInfo info : giftSameInfos) {
                    if (giftSenderAndInfo.giftInfo.giftId == info.giftInfo.giftId
                            && giftSenderAndInfo.repeatId.equals(info.repeatId)
                            && giftSenderAndInfo.senderProfile.getIdentifier().equals(info.senderProfile.getIdentifier())) {
                        giftSameInfos.add(info);
                    }
                }
                giftSenderAndInfoList.removeAll(giftSameInfos);
                for (GiftSenderAndInfo info : giftSameInfos) {
                    showGift(info.giftInfo, info.repeatId, info.senderProfile);
                }
            }
        }
    };

    public void showGift(GiftInfo giftInfo, String repeatId, TIMUserProfile profile) {
        GiftRepeatItemView avaliableView = getAvaliableView(giftInfo, repeatId, profile);
        if (avaliableView == null) {
            GiftSenderAndInfo info = new GiftSenderAndInfo();
            info.giftInfo = giftInfo;
            info.senderProfile = profile;
            info.repeatId = repeatId;
            giftSenderAndInfoList.add(info);
        } else {
            avaliableView.showGift(giftInfo, repeatId, profile);
        }
    }

    private GiftRepeatItemView getAvaliableView(GiftInfo giftInfo, String repeatId, TIMUserProfile profile) {

        if (item0.isAvaliable(giftInfo, repeatId, profile)) {
            return item0;
        }

        if (item1.isAvaliable(giftInfo, repeatId, profile)) {
            return item1;
        }

        if (item0.getVisibility() == INVISIBLE) {
            return item0;
        }
        if (item1.getVisibility() == INVISIBLE) {
            return item1;
        }
        return null;
    }
}
