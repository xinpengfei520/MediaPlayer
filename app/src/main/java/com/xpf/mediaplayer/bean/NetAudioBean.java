package com.xpf.mediaplayer.bean;

import java.util.List;

/**
 * Created by xinpengfei on 2016/9/28.
 * Function : NetAudioBean
 */
public class NetAudioBean {

    /**
     * count : 4362
     * np : 1464579826
     */

    private InfoEntity info;
    /**
     * comment : 32
     * tags : [{"id":1,"name":"搞笑"},{"id":60,"name":"吐槽"},{"id":61,"name":"恶搞"}]
     * bookmark : 28
     * text : 有一种妹妹叫做别人家的妹妹。。。看到最后我哈哈哈了！
     * image : {"medium":[],"big":["http://wimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35_1.jpg","http://dimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35_1.jpg"],"download_url":["http://wimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35_d.jpg","http://dimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35_d.jpg","http://wimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35.jpg","http://dimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35.jpg"],"height":8836,"width":634,"small":[]}
     * up : 403
     * share_url : http://b.f.budejie.com/share/18704247.html?wx.qq.com
     * down : 40
     * forward : 34
     * top_comment : {"voicetime":0,"precid":0,"content":"一想到是个男的写的，我瞬间吓尿了","like_count":"58","u":{"header":["http://wimg.spriteapp.cn/profile/large/2015/11/26/5655fa253482b_mini.jpg","http://dimg.spriteapp.cn/profile/large/2015/11/26/5655fa253482b_mini.jpg"],"sex":"m","uid":"16666001","name":"惔問茳煳倳"},"preuid":0,"voiceuri":"","id":53279069}
     * u : {"header":["http://wimg.spriteapp.cn/profile/large/2016/03/23/56f21ebb2749e_mini.jpg","http://dimg.spriteapp.cn/profile/large/2016/03/23/56f21ebb2749e_mini.jpg"],"is_v":false,"uid":"11986406","is_vip":false,"name":"你有种别动手"}
     * passtime : 2016-05-30 14:45:47
     * type : image
     * id : 18704247
     */

    private List<ListEntity> list;

    public void setInfo(InfoEntity info) {
        this.info = info;
    }

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public InfoEntity getInfo() {
        return info;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public static class InfoEntity {
        private int count;
        private int np;

        public void setCount(int count) {
            this.count = count;
        }

        public void setNp(int np) {
            this.np = np;
        }

        public int getCount() {
            return count;
        }

        public int getNp() {
            return np;
        }
    }

    public static class ListEntity {
        private String comment;
        private String bookmark;
        private String text;
        /**
         * medium : []
         * big : ["http://wimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35_1.jpg","http://dimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35_1.jpg"]
         * download_url : ["http://wimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35_d.jpg","http://dimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35_d.jpg","http://wimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35.jpg","http://dimg.spriteapp.cn/ugc/2016/05/29/574a5528afa35.jpg"]
         * height : 8836
         * width : 634
         * small : []
         */

        private ImageEntity image;
        private String up;
        private String share_url;
        private int down;
        private String forward;
        /**
         * voicetime : 0
         * precid : 0
         * content : 一想到是个男的写的，我瞬间吓尿了
         * like_count : 58
         * u : {"header":["http://wimg.spriteapp.cn/profile/large/2015/11/26/5655fa253482b_mini.jpg","http://dimg.spriteapp.cn/profile/large/2015/11/26/5655fa253482b_mini.jpg"],"sex":"m","uid":"16666001","name":"惔問茳煳倳"}
         * preuid : 0
         * voiceuri :
         * id : 53279069
         */

        private TopCommentEntity top_comment;
        /**
         * header : ["http://wimg.spriteapp.cn/profile/large/2016/03/23/56f21ebb2749e_mini.jpg","http://dimg.spriteapp.cn/profile/large/2016/03/23/56f21ebb2749e_mini.jpg"]
         * is_v : false
         * uid : 11986406
         * is_vip : false
         * name : 你有种别动手
         */

        private UEntity u;
        private String passtime;
        private String type;
        private String id;
        /**
         * id : 1
         * name : 搞笑
         */

        private List<TagsEntity> tags;

        private VideoEntity  video;
        private GifEntity gif;

        public VideoEntity getVideo() {
            return video;
        }

        public void setVideo(VideoEntity video) {
            this.video = video;
        }

        public GifEntity getGif() {
            return gif;
        }

        public void setGif(GifEntity gif) {
            this.gif = gif;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public void setBookmark(String bookmark) {
            this.bookmark = bookmark;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setImage(ImageEntity image) {
            this.image = image;
        }

        public void setUp(String up) {
            this.up = up;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public void setDown(int down) {
            this.down = down;
        }

        public void setForward(String forward) {
            this.forward = forward;
        }

        public void setTop_comment(TopCommentEntity top_comment) {
            this.top_comment = top_comment;
        }

        public void setU(UEntity u) {
            this.u = u;
        }

        public void setPasstime(String passtime) {
            this.passtime = passtime;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTags(List<TagsEntity> tags) {
            this.tags = tags;
        }

        public String getComment() {
            return comment;
        }

        public String getBookmark() {
            return bookmark;
        }

        public String getText() {
            return text;
        }

        public ImageEntity getImage() {
            return image;
        }

        public String getUp() {
            return up;
        }

        public String getShare_url() {
            return share_url;
        }

        public int getDown() {
            return down;
        }

        public String getForward() {
            return forward;
        }

        public TopCommentEntity getTop_comment() {
            return top_comment;
        }

        public UEntity getU() {
            return u;
        }

        public String getPasstime() {
            return passtime;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public List<TagsEntity> getTags() {
            return tags;
        }

        public static class GifEntity{
            private List<String> download_url;
            private List<String> gif_thumbnail;
            private int height;
            private int width;
            private List<String> images;

            public List<String> getDownload_url() {
                return download_url;
            }

            public void setDownload_url(List<String> download_url) {
                this.download_url = download_url;
            }

            public List<String> getGif_thumbnail() {
                return gif_thumbnail;
            }

            public void setGif_thumbnail(List<String> gif_thumbnail) {
                this.gif_thumbnail = gif_thumbnail;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public List<String> getImages() {
                return images;
            }

            public void setImages(List<String> images) {
                this.images = images;
            }
        }

        public static class ImageEntity {
            private int height;
            private int width;
            private List<?> medium;
            private List<String> big;
            private List<String> download_url;
            private List<?> small;

            public void setHeight(int height) {
                this.height = height;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public void setMedium(List<?> medium) {
                this.medium = medium;
            }

            public void setBig(List<String> big) {
                this.big = big;
            }

            public void setDownload_url(List<String> download_url) {
                this.download_url = download_url;
            }

            public void setSmall(List<?> small) {
                this.small = small;
            }

            public int getHeight() {
                return height;
            }

            public int getWidth() {
                return width;
            }

            public List<?> getMedium() {
                return medium;
            }

            public List<String> getBig() {
                return big;
            }

            public List<String> getDownload_url() {
                return download_url;
            }

            public List<?> getSmall() {
                return small;
            }
        }
        public static class VideoEntity {
            private List<String> download;
            private int duration;
            private int height;
            private int playcount;
            private List<String> thumbnail;
            private List<String> video;
            private int width;

            public List<String> getDownload() {
                return download;
            }

            public void setDownload(List<String> download) {
                this.download = download;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getPlaycount() {
                return playcount;
            }

            public void setPlaycount(int playcount) {
                this.playcount = playcount;
            }

            public List<String> getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(List<String> thumbnail) {
                this.thumbnail = thumbnail;
            }

            public List<String> getVideo() {
                return video;
            }

            public void setVideo(List<String> video) {
                this.video = video;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            @Override
            public String toString() {
                return "VideoEntity{" +
                        "download=" + download +
                        ", duration=" + duration +
                        ", height=" + height +
                        ", playcount=" + playcount +
                        ", thumbnail=" + thumbnail +
                        ", video=" + video +
                        ", width=" + width +
                        '}';
            }
        }

        public static class TopCommentEntity {
            private int voicetime;
            private int precid;
            private String content;
            private String like_count;
            /**
             * header : ["http://wimg.spriteapp.cn/profile/large/2015/11/26/5655fa253482b_mini.jpg","http://dimg.spriteapp.cn/profile/large/2015/11/26/5655fa253482b_mini.jpg"]
             * sex : m
             * uid : 16666001
             * name : 惔問茳煳倳
             */

            private UEntity u;
            private int preuid;
            private String voiceuri;
            private int id;

            public void setVoicetime(int voicetime) {
                this.voicetime = voicetime;
            }

            public void setPrecid(int precid) {
                this.precid = precid;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public void setLike_count(String like_count) {
                this.like_count = like_count;
            }

            public void setU(UEntity u) {
                this.u = u;
            }

            public void setPreuid(int preuid) {
                this.preuid = preuid;
            }

            public void setVoiceuri(String voiceuri) {
                this.voiceuri = voiceuri;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getVoicetime() {
                return voicetime;
            }

            public int getPrecid() {
                return precid;
            }

            public String getContent() {
                return content;
            }

            public String getLike_count() {
                return like_count;
            }

            public UEntity getU() {
                return u;
            }

            public int getPreuid() {
                return preuid;
            }

            public String getVoiceuri() {
                return voiceuri;
            }

            public int getId() {
                return id;
            }

            public static class UEntity {
                private String sex;
                private String uid;
                private String name;
                private List<String> header;

                public void setSex(String sex) {
                    this.sex = sex;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public void setHeader(List<String> header) {
                    this.header = header;
                }

                public String getSex() {
                    return sex;
                }

                public String getUid() {
                    return uid;
                }

                public String getName() {
                    return name;
                }

                public List<String> getHeader() {
                    return header;
                }
            }
        }

        public static class UEntity {
            private boolean is_v;
            private String uid;
            private boolean is_vip;
            private String name;
            private List<String> header;

            public void setIs_v(boolean is_v) {
                this.is_v = is_v;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public void setIs_vip(boolean is_vip) {
                this.is_vip = is_vip;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setHeader(List<String> header) {
                this.header = header;
            }

            public boolean isIs_v() {
                return is_v;
            }

            public String getUid() {
                return uid;
            }

            public boolean isIs_vip() {
                return is_vip;
            }

            public String getName() {
                return name;
            }

            public List<String> getHeader() {
                return header;
            }
        }

        public static class TagsEntity {
            private int id;
            private String name;

            public void setId(int id) {
                this.id = id;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }
        }
    }
}
