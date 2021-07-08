package com.mul_alexautoprogramm.bulletinboardjavaversion.adapters;

import com.mul_alexautoprogramm.bulletinboardjavaversion.NewPost;

import java.util.List;

public interface DataSender {

     void onDataRecived(List<NewPost> newPostList);

}
