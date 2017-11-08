package com.dryseed.dslive.livelist;

import com.dryseed.dslive.utils.net.BaseRequest;
import com.dryseed.dslive.utils.net.ResponseObject;

import java.io.IOException;


/**
 * Created by caiminming on 2017/11/8.
 */

public class GetLiveListRequest extends BaseRequest {
    private static final String HOST = "http://imoocbearlive.butterfly.mopaasapp.com/roomServlet?action=getList";

    public static class LiveListParam {
        public int pageIndex;

        public String toUrlParam() {
            return "&=pageIndex" + pageIndex;
        }
    }

    public String getUrl(LiveListParam param) {
        return HOST + param.toUrlParam();
    }

    @Override
    protected void onFail(IOException e) {
        sendFailMsg(-100, e.toString());
    }

    @Override
    protected void onResponseSuccess(String body) {
        /*
            {
              "code": "1",
              "errCode": "",
              "errMsg": "",
              "data": [
                {
                  "roomId": 251,
                  "userId": "weizhiyang",
                  "userName": "Jack",
                  "userAvatar": "http://oxwynt3qn.bkt.clouddn.com/weizhiyang_1508153732678_avatar",
                  "liveCover": "null",
                  "liveTitle": "",
                  "watcherNums": 0
                },
                {
                  "roomId": 253,
                  "userId": "blive",
                  "userName": "blive",
                  "userAvatar": "",
                  "liveCover": "http://oe0i3jf0i.bkt.clouddn.com/_1509351037717_avatar",
                  "liveTitle": "鱼的直播",
                  "watcherNums": 0
                },
                {
                  "roomId": 257,
                  "userId": "",
                  "userName": "",
                  "userAvatar": "",
                  "liveCover": "",
                  "liveTitle": "",
                  "watcherNums": 0
                },
                {
                  "roomId": 258,
                  "userId": "dryseedcai123",
                  "userName": "dryseedcai123",
                  "userAvatar": "",
                  "liveCover": "http://oe0i3jf0i.bkt.clouddn.com/_1510111690237_avatar",
                  "liveTitle": "ds",
                  "watcherNums": 0
                }
              ]
            }
         */
        LiveListResponseObj liveListresponseObject = gson.fromJson(body, LiveListResponseObj.class);
        if (liveListresponseObject == null) {
            sendFailMsg(-101, "数据格式错误");
            return;
        }

        if (liveListresponseObject.code.equals(ResponseObject.CODE_SUCCESS)) {
            sendSuccMsg(liveListresponseObject.data);
        } else if (liveListresponseObject.code.equals(ResponseObject.CODE_FAIL)) {
            sendFailMsg(Integer.valueOf(liveListresponseObject.errCode), liveListresponseObject.errMsg);
        }
    }

    @Override
    protected void onResponseFail(int code) {
        sendFailMsg(code, "服务器异常");
    }

}
