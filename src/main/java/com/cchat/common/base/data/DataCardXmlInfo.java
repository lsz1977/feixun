package com.cchat.common.base.data;



/*
 * 用户信息解析 http://cardxml000001.dorpost.com/SaveXmlServer/xml/regxmls/10150420141021194649.xml
 * <my>
 *	<card>101504</card>
 *  <nick>hhool</nick>
 *  <sex>男</sex>
 *  <signature>如何是好</signature>
 *  <isafe>no</isafe>
 *  <head>http://cardxml000001.dorpost.com/UploadServer/upload/heads/101504201501221745080.jpg</head>
 *  <location>广东省 珠海市</location>
 *  <spic>
 *    http://sharexml000001.dorpost.com/upload/shares/101504201501161232310.jpg;http://sharexml000001.dorpost.com/upload/shares/101504201501161232311.jpg;http://sharexml000001.dorpost.com/upload/shares/101504201501161232312.jpg;http://sharexml000001.dorpost.com/upload/shares/101504201501161232313.jpg;http://sharexml000001.dorpost.com/upload/shares/101504201501161232314.jpg;http://sharexml000001.dorpost.com/upload/shares/101504201501161232315.jpg;
 *  </spic>
 *  <localHead/>
 *  <jid>xxxx@null.dorpost.com</jid>
 *</my>
 */

import android.os.Parcel;
import android.os.Parcelable;

public class DataCardXmlInfo extends DataCardEntry implements Parcelable, IToXml {
    public final static String DEFAULT_HEAD = "defaultHead";
    private String mNick;            // 昵称
    private String mArea;            // 地址
    private String mHeadImgUrl;     // 头像照片地址
    private String mShareUrl;       // 分享照片url  //TODO:hhool 保留
    private String mSex;             // 性别         //TODO:hhool 整形 0 女 1 男
    private String mSignature;      // 签名
    private String mJid;             //聊天域名
    private boolean mIsSafe;        //是否密码保护

    public DataCardXmlInfo(Parcel in) {
        super(in);
        mNick = in.readString();
        mArea = in.readString();
        mHeadImgUrl = in.readString();
        mShareUrl = in.readString();
        mSex = in.readString();
        mSignature = in.readString();
        mJid = in.readString();
        mIsSafe = (in.readInt() == 1) ? true : false;
    }

    public DataCardXmlInfo() {

    }

    public DataCardXmlInfo setCardEntry(DataCardEntry cardEntry) {
        if (cardEntry.getCard() != null) {
            setCard(cardEntry.getCard());
        }
        if (cardEntry.getCardXmlUrl() != null) {
            setCardXmlUrl(cardEntry.getCardXmlUrl());
        }
        if (cardEntry.getStyleLoc() != null) {
            setStyleLoc(cardEntry.getStyleLoc());
        }
        return DataCardXmlInfo.this;
    }

    public DataCardEntry getCardEntry() {
        DataCardEntry cardEntry = new DataCardEntry();
        cardEntry.setCard(getCard());
        cardEntry.setCardXmlUrl(getCardXmlUrl());
        cardEntry.setStyleLoc(getStyleLoc());
        return cardEntry;
    }

    public String getCard() {
        return super.getCard();
    }

    public DataCardXmlInfo setCard(String card) {
        if (card != null)
            super.setCard(card);
        return DataCardXmlInfo.this;
    }

    public DataCardXmlInfo setCardXmlUrl(String cardXml) {
        super.setCardXmlUrl(cardXml);
        return DataCardXmlInfo.this;
    }

    public String getNick() {
        return mNick;
    }

    public DataCardXmlInfo setNick(String nick) {
        if (nick != null)
            this.mNick = nick;
        return DataCardXmlInfo.this;
    }

    public String getArea() {
        return mArea;
    }

    public DataCardXmlInfo setArea(String area) {
        if (area != null)
            this.mArea = area;
        return DataCardXmlInfo.this;
    }

    public String getHeadUrl() {
        return mHeadImgUrl;
    }

    public DataCardXmlInfo setHeadUrl(String headUrl) {
        if ("defaultHead".equals(headUrl)) {
            this.mHeadImgUrl = null;
        } else if (headUrl != null) {
            this.mHeadImgUrl = headUrl;
        }
        return DataCardXmlInfo.this;
    }

    public String getHeadThumbUrl() {
        if (mHeadImgUrl != null && mHeadImgUrl.length() >= 12) {
            String first = mHeadImgUrl.substring(0, mHeadImgUrl.lastIndexOf('.'));
            String suffix = mHeadImgUrl.substring(mHeadImgUrl.lastIndexOf('.'), mHeadImgUrl.length());
            return first + "_thumb" + suffix;
        }
        return "";
    }

    public String getDefaultHead() {
        return "defaultHead";
    }

    public String getShareUrl() {
        return mShareUrl;
    }

    public DataCardXmlInfo setShareUrl(String shareUrl) {
        if (shareUrl != null)
            this.mShareUrl = shareUrl;
        return DataCardXmlInfo.this;
    }

    public String getSex() {
        return mSex;
    }

    public DataCardXmlInfo setSex(String sex) {
        if (sex != null)
            this.mSex = sex;
        return DataCardXmlInfo.this;
    }

    public String getSignature() {
        return mSignature;
    }

    public DataCardXmlInfo setSignature(String signature) {
        if (signature != null)
            this.mSignature = signature;
        return DataCardXmlInfo.this;
    }

    public String getJid() {
        return mJid;
    }

    public DataCardXmlInfo setJid(String jid) {
        if (jid != null)
            this.mJid = jid;
        return DataCardXmlInfo.this;
    }

    public boolean getIsSafe() {
        return mIsSafe;
    }

    public DataCardXmlInfo setIsSafe(boolean isSafe) {
        this.mIsSafe = isSafe;
        return DataCardXmlInfo.this;
    }

    public void mergeFrom(DataCardXmlInfo info) {
        if (info == null)
            return;
        super.mergeFrom(info);
        setNick(info.getNick());
        setArea(info.getArea());
        setHeadUrl(info.getHeadUrl());
        setShareUrl(info.getShareUrl());
        setSex(info.getSex());
        setSignature(info.getSignature());
        setIsSafe(info.getIsSafe());
        setJid(info.getJid());
    }

    @Override
    public DataCardXmlInfo clone() {
        DataCardXmlInfo info = new DataCardXmlInfo();
        info.setArea(this.getArea());
        info.setCard(this.getCard());
        info.setCardXmlUrl(this.getCardXmlUrl());
        info.setHeadUrl(this.getHeadUrl());
        info.setIsSafe(this.getIsSafe());
        info.setJid(this.getJid());
        info.setNick(this.getNick());
        info.setSex(this.getSex());
        info.setShareUrl(this.getShareUrl());
        info.setSignature(this.getSignature());
        info.setStyleLoc(this.getStyleLoc());
        return info;
    }

    @Override
    public String getDisplayName() {
        String displayName;
        if (this.getNick() != null && this.getNick().length() > 0) {
            displayName = this.getNick();
        } else {
            displayName = this.getCard();
        }
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataCardXmlInfo) {
            DataCardEntry cardInfo = (DataCardEntry) o;
            if (cardInfo.getCard() != null && super.getCard() != null) {
                if (cardInfo.getCard().equals(super.getCard())) {
                    return true;
                }
            }
        } else if (o instanceof DataCardEntry) {
            return super.equals(o);
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mNick);
        dest.writeString(mArea);
        dest.writeString(mHeadImgUrl);
        dest.writeString(mShareUrl);
        dest.writeString(mSex);
        dest.writeString(mSignature);
        dest.writeString(mJid);
        dest.writeInt(mIsSafe ? 1 : 0);
    }

    public static final Creator<DataCardXmlInfo> CREATOR = new Creator<DataCardXmlInfo>() {
        @Override
        public DataCardXmlInfo createFromParcel(Parcel in) {
            return new DataCardXmlInfo(in);
        }

        @Override
        public DataCardXmlInfo[] newArray(int size) {
            return new DataCardXmlInfo[size];
        }
    };

    @Override
    public String toXml(boolean isUseHtmlEncode, boolean isUseUriEncode) {
        String xmlContent = null;
        try {
            StringBuilder buf = new StringBuilder();
            buf.append("<my>");
            buf.append("<card>").append(getCard()).append("</card>");
            buf.append("<cardXml>").append((getCardXmlUrl() == null) ? "" : getCardXmlUrl()).append("</cardXml>");
            buf.append("<nick>").append((getNick() == null) ? "" : getNick()).append("</nick>");
            buf.append("<sex>").append((getSex() == null) ? "" : getSex()).append("</sex>");
            String signature = (this.getSignature() == null) ? "" : this.getSignature();
            if (isUseHtmlEncode) {
                signature = Hbutils.htmlEncode(signature);
            }
            if (isUseUriEncode) {
                signature = Hbutils.URIEncoder(signature, null);
            }
            buf.append("<signature>").append(signature).append("</signature>");
            buf.append("<isafe>").append((getIsSafe()) ? "yes" : "no").append("</isafe>");
            buf.append("<head>").append((getHeadUrl() == null) ? "" : getHeadUrl()).append("</head>");
            buf.append("<location>").append((getArea() == null) ? "" : getArea()).append("</location>");
            buf.append("<jid>").append((getJid() == null) ? "" : getJid()).append("</jid>");
            buf.append("</my>");
            xmlContent = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlContent;
    }
}
 