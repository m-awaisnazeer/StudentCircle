package com.communisolve.studentcircle.callbacks;

import com.communisolve.studentcircle.Model.PostModel;

public interface PostItemClickListener {
    void onPostItemCLick(PostModel postModel,boolean isCommentedOnPost);
}
