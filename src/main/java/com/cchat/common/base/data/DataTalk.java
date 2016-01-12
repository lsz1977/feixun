/**
 * Project: Callga
 * Create At 2015-1-30.
 *
 * @author hhool
 */
package com.cchat.common.base.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DataTalk implements Parcelable, Cloneable, IToXml {
    private Func mFun;		              /*onMsg*/
    private String mTimeId; 	          /*2015-01-29 21:54:51:051*/
    private String mKeyWord;            //人海关键词
    private String mContent;            //文本信息
    private ContentType mContentType;  //信息属性
    private String mAttachLocal;       //携带的附件的本地地址
    private String mAttachNet;         //携带的附件网络地址

    public DataTalk(Parcel in) {
        mFun = Func.valueOf(in.readString());
        mTimeId = in.readString();
        mKeyWord = in.readString();
        mContent = in.readString();
        mContentType = ContentType.valueOf(in.readString());
        mAttachLocal = in.readString();
        mAttachNet = in.readString();
    }

    public DataTalk() {

    }

    /**
     * @return the fun
     */
    public Func getFun() {
        return mFun;
    }

    /**
     * @param fun the fun to set
     */
    public void setFun(Func fun) {
        mFun = fun;
    }

    /**
     * @return the timeId
     */
    public String getTimeId() {
        return mTimeId;
    }

    /**
     * @param timeId the timeId to set
     */
    public void setTimeId(String timeId) {
        mTimeId = timeId;
    }

    /**
     * @return the mKeyWord
     */
    public String getKeyWord() {
        return mKeyWord;
    }

    /**
     * @param keyWord the mKeyWord to set
     */
    public void setKeyWord(String keyWord) {
        this.mKeyWord = keyWord;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return mContent;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        mContent = content;
    }

    /**
     * @return the contentType
     */
    public ContentType getContentType() {
        return mContentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(ContentType contentType) {
        mContentType = contentType;
    }

    /**
     * @return the attachments
     */
    public String getAttachLocal() {
        return mAttachLocal;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachLocal(String attachments) {
        mAttachLocal = attachments;
    }

    public String getAttachNet() {
        return mAttachNet;
    }

    public void setAttachNet(String attachNet) {
        mAttachNet = attachNet;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DataTalk && ((DataTalk) o).getTimeId().equals(getTimeId());
    }

    @Override
    public String toXml(boolean isUseHtmlEncode, boolean isUseUriEncode) {
        StringBuilder buf = new StringBuilder();
        buf.append("<talk ").append("func").append("=\"").append(this.getFun().toString()).append("\"");
        if (!this.getFun().equals(Func.PushBlack) && !this.getFun().equals(Func.RemoveBlack)) {
            buf.append(" ").append("timeId").append("=\"").append(this.getTimeId()).append("\"");
        }
        if (this.getFun().equals(Func.OnAnonMsg)) {
            buf.append(" ").append("keyWord").append("=\"").append((this.mKeyWord == null) ? "" : mKeyWord).append("\"");
        }
        buf.append(">");

        if (this.getFun().equals(Func.OnMsg) || this.getFun().equals(Func.OnAnonMsg)) {
            String result = (this.getContent() == null) ? "" : this.getContent();
            if (this.getContentType().equals(ContentType.text) || (this.getContentType().equals(ContentType.emoji))) {
                if (isUseHtmlEncode) {
                    result = Hbutils.htmlEncode(result);
                }
                if (isUseUriEncode) {
                    result = Hbutils.URIEncoder(result, null);
                }
            } else {
                //do nothing
            }
            buf.append("<content ").append("type=").append("\"").append(this.getContentType().toString()).append("\"").append(">")
                    .append(result).append("</content>");
            result = (this.getAttachNet() == null) ? "" : this.getAttachNet();
            buf.append("<attachments>").append(result).append("</attachments>");
        }
        buf.append("</talk>");
        return buf.toString();
    }

    public static final Creator<DataTalk> CREATOR = new Creator<DataTalk>() {
        @Override
        public DataTalk createFromParcel(Parcel in) {
            return new DataTalk(in);
        }

        @Override
        public DataTalk[] newArray(int size) {
            return new DataTalk[size];
        }
    };

    @Override
    public DataTalk clone() {
        DataTalk dataTalk = new DataTalk();
        dataTalk.mFun = this.mFun;
        dataTalk.mTimeId = this.mTimeId;
        dataTalk.mKeyWord = this.mKeyWord;
        dataTalk.mContent = this.mContent;
        dataTalk.mContentType = this.mContentType;
        dataTalk.mAttachLocal = this.mAttachLocal;
        dataTalk.mAttachNet = this.mAttachNet;
        return dataTalk;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mFun == null) {
            mFun = Func.none;
        }
        dest.writeString(mFun.toString());
        dest.writeString(mTimeId);
        dest.writeString(mKeyWord);
        dest.writeString(mContent);
        if (mContentType == null) {
            mContentType = ContentType.none;
        }
        dest.writeString(mContentType.toString());
        dest.writeString(mAttachLocal);
        dest.writeString(mAttachNet);
    }

    public enum Func {
        none,
        OnMsg,
        OnAnonMsg,
        OnReceipt,
        PushBlack,
        RemoveBlack
    }

    public enum ContentType {
        none,
        image,
        voice,
        text,
        location,
        emoji,
        file
    }
}
