package dhiraj.com.chatapplication;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by dhira on 27-04-2017.
 */

public class Message implements Serializable {
    private String sentBy, msg,imageFile, sentTime,messageId,sentUserId;

    public String getSentUserId() {
        return sentUserId;
    }

    public void setSentUserId(String sentUserId) {
        this.sentUserId = sentUserId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
