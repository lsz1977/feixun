/**
 * Project: Callga
 * Create At 2015-1-30.
 * @author hhool
 */
package com.cchat.common.base.data.XmlParse;

import com.cchat.common.base.data.DataTalk;
import com.cchat.common.base.data.Hbutils;
import com.cchat.common.base.data.XmlData.DataChatMessage;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/*
 * <message xmls="jabber:client" type="chat" to="100000" from="101504">
 *   <body>
 *     <talk func="onMsg" timeId="2015-01-29 21:54:51:051">
 *        <content type="emoji">[猪头]</content>
 *        <attachments></attachments>
 *     </talk>
 *   </body>
 *</message>
 */

public class XmlParseChatMessage extends XmlParseBase {
    public XmlParseChatMessage() {
        super();
    }

    @Override
    protected XmlParseBase.XMLHandler getHandler() {
        return new ChatOffMessageHandler();
    }

    public enum Node {
        body, talk, card, nick, content, timeId, func,
        attachments, message, xmls, type, to, from, keyWord
    }

    protected class ChatOffMessageHandler extends XmlParseBase.XMLHandler {
        private DataChatMessage mDataChatMessage;
        private DataTalk mTalk;

        ChatOffMessageHandler() {
            mDataChatMessage = new DataChatMessage();
            mTalk = new DataTalk();
        }

        @Override
        public DataChatMessage getResult() {
            return mDataChatMessage;
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (localName.equals(Node.message.toString())) {
                int nIndex = attributes.getIndex(Node.from.toString());
                if (nIndex != -1) {
                    mDataChatMessage.setFrom(attributes.getValue(nIndex));
                }
                nIndex = attributes.getIndex(Node.to.toString());
                if (nIndex != -1) {
                    mDataChatMessage.setTo(attributes.getValue(nIndex));
                }
                nIndex = attributes.getIndex(Node.type.toString());
                if (nIndex != -1) {
                    mDataChatMessage.setType(attributes.getValue(nIndex));
                }
            } else if (localName.equals(Node.content.toString())) {
                mTalk.setContentType(DataTalk.ContentType.valueOf(attributes.getValue(attributes.getIndex(Node.type.toString()))));
            } else if (localName.equals(Node.talk.toString())) {
                mDataChatMessage.setTalk(mTalk);
                mTalk.setFun(DataTalk.Func.valueOf(attributes.getValue(Node.func.toString())));
                if (attributes.getValue(Node.func.toString()).equals(DataTalk.Func.OnAnonMsg.toString())) {
                    mTalk.setKeyWord(attributes.getValue(Node.keyWord.toString()));
                    mTalk.setTimeId(attributes.getValue(Node.timeId.toString()));
                } else {
                    mTalk.setTimeId(attributes.getValue(Node.timeId.toString()));
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (localName.equals(Node.content.toString())) {
                String content = mBuilder.toString();
                if (mTalk.getContentType().equals(DataTalk.ContentType.text) || mTalk.getContentType().equals(DataTalk.ContentType.emoji)) {
                    content = Hbutils.URIDecoder(content);
                }
                mTalk.setContent(content);
            } else if (localName.equals(Node.attachments.toString())) {
                String attachments = mBuilder.toString();
                if (mTalk.getContentType().equals(DataTalk.ContentType.image)) {
                    if (attachments.length() > 0 && attachments.endsWith(";")) {
                        attachments = attachments.substring(0, attachments.length() - 1);
                    }
                } else if (mTalk.getContentType().equals(DataTalk.ContentType.location)) {
                    attachments = mBuilder.toString();
                }
                mTalk.setAttachNet(attachments);
            }
        }
    }
}
