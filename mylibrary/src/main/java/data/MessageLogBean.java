package data;

import android.os.Parcel;
import android.os.Parcelable;

public class MessageLogBean implements Parcelable {

    public String applicationId;
    public String id;
    public String level;
    public String type;
    public String src;
    public String subject;
    public String content;
    public String url;
    public String createTime;
    public String tag;

    public MessageLogBean(String applicationId, String id, String level, String type, String src, String subject, String content, String url, String createTime, String tag) {
        this.applicationId = applicationId;
        this.id = id;
        this.level = level;
        this.type = type;
        this.src = src;
        this.subject = subject;
        this.content = content;
        this.url = url;
        this.createTime = createTime;
        this.tag = tag;
    }

    protected MessageLogBean(Parcel in) {
        applicationId = in.readString();
        createTime = in.readString();
        id = in.readString();
        level = in.readString();
        type = in.readString();
        src = in.readString();
        subject = in.readString();
        content = in.readString();
        url = in.readString();
        tag = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(applicationId);
        dest.writeString(createTime);
        dest.writeString(id);
        dest.writeString(level);
        dest.writeString(type);
        dest.writeString(src);
        dest.writeString(subject);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(tag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageLogBean> CREATOR = new Creator<MessageLogBean>() {
        @Override
        public MessageLogBean createFromParcel(Parcel in) {
            return new MessageLogBean(in);
        }

        @Override
        public MessageLogBean[] newArray(int size) {
            return new MessageLogBean[size];
        }
    };

    public String getApplicationId() {
        return applicationId;
    }

    public MessageLogBean setApplicationId(String applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getId() {
        return id;
    }

    public MessageLogBean setId(String id) {
        this.id = id;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public MessageLogBean setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getType() {
        return type;
    }

    public MessageLogBean setType(String type) {
        this.type = type;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public MessageLogBean setSrc(String src) {
        this.src = src;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public MessageLogBean setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MessageLogBean setContent(String content) {
        this.content = content;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MessageLogBean setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public MessageLogBean setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public MessageLogBean setTag(String tag) {
        this.tag = tag;
        return this;
    }




}
