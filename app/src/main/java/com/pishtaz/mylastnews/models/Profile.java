package com.pishtaz.mylastnews.models;

/**
 * Created by Arash on 14/07/2015.
 */
public class Profile {





    String userName;
    String profileImage;
    String coverImage;
    String thumbCoverImage;
    String albumCount;
    String photoCount;


        public String getUserName() {
                return userName;
        }

        public void setUserName(String userName) {
                this.userName = userName;
        }

        public String getProfileImage() {
                return profileImage;
        }

        public void setProfileImage(String profileImage) {
                this.profileImage = profileImage;
        }

        public String getCoverImage() {
                return coverImage;
        }

        public void setCoverImage(String coverImage) {
                this.coverImage = coverImage;
        }

        public String getThumbCoverImage() {
                return thumbCoverImage;
        }

        public void setThumbCoverImage(String thumbCoverImage) {
                this.thumbCoverImage = thumbCoverImage;
        }

        public String getAlbumCount() {
                return albumCount;
        }

        public void setAlbumCount(String albumCount) {
                this.albumCount = albumCount;
        }

        public String getPhotoCount() {
                return photoCount;
        }

        public void setPhotoCount(String photoCount) {
                this.photoCount = photoCount;
        }
}
