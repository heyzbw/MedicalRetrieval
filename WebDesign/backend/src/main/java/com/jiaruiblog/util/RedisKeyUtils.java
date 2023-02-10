package com.jiaruiblog.util;

/**
 * @ClassName RedisKeyUtils
 * @Description RedisKeyUtils
 * @Author luojiarui
 * @Date 2022/8/14 17:13
 * @Version 1.0
 **/
public class RedisKeyUtils {

    private RedisKeyUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getSearchHistoryKey(String userid) {
        return userid;
    }


    //保存用户点赞数据的key
    public static final String MAP_KEY_USER_LIKED = "MAP_USER_LIKED";
    //保存用户被点赞数量的key
    public static final String MAP_KEY_USER_LIKED_COUNT = "MAP_USER_LIKED_COUNT";

    /**
     * 拼接被点赞的用户id和点赞的人的id作为key。格式 222222::333333
     * @param likedUserId 被点赞的人id
     * @param likedPostId 点赞的人的id
     * @return
     */
    public static String getLikedKey(String likedUserId, String likedPostId){
        StringBuilder builder = new StringBuilder();
        builder.append(likedUserId);
        builder.append("::");
        builder.append(likedPostId);
        return builder.toString();
    }



}
