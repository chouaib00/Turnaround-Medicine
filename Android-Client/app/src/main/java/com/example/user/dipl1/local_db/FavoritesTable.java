package com.example.user.dipl1.local_db;

public class FavoritesTable {
    private int _id;
    private int favorite_post;

    public FavoritesTable() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getFavorite_post() {
        return favorite_post;
    }

    public void setFavorite_post(int favorite_post) {
        this.favorite_post = favorite_post;
    }
}
