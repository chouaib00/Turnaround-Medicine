package com.example.user.dipl1.local_db;

public class PhotoTable {

    private int photo_id;
    private byte[] photo;
    private String photo_date;

    public PhotoTable() {}

    public PhotoTable(PhotoBuilder photoBuilder) {
        this.photo_id = photoBuilder.photo_id;
        this.photo = photoBuilder.photo;
        this.photo_date = photoBuilder.photo_date;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public String getPhoto_date() {
        return photo_date;
    }

    public static class PhotoBuilder{
        private int photo_id;
        private byte[] photo;
        private String photo_date;

        public PhotoBuilder photo_id(int photo_id){
            this.photo_id = photo_id;
            return this;
        }

        public PhotoBuilder photo(byte[] photo){
            this.photo = photo;
            return this;
        }

        public PhotoBuilder photo_date(String photo_date){
            this.photo_date = photo_date;
            return this;
        }

        public PhotoTable build(){
            return new PhotoTable(this);
        }
    }
}
