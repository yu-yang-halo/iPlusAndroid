package cn.lztech.bean;

import java.util.List;

/**
 * Created by Administrator on 2015/9/2.
 */
public class AppTagGson {
    private List<AppTagInfo> tagList;

    public List<AppTagInfo> getTagList() {
        return tagList;
    }

    public void setTagList(List<AppTagInfo> tagList) {
        this.tagList = tagList;
    }
    public  class AppTagInfo{
        private String userName;
        private String appTag;

        public String getAppTag() {
            return appTag;
        }

        public void setAppTag(String appTag) {
            this.appTag = appTag;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}

