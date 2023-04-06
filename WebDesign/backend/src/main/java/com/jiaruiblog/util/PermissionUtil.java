package com.jiaruiblog.util;

import com.jiaruiblog.auth.PermissionEnum;

import javax.servlet.http.HttpServletRequest;

public class PermissionUtil {
    public static PermissionEnum getUserPermission(HttpServletRequest request) {
        int role = (int) request.getAttribute("permission");
        // 根据角色映射到 PermissionEnum
        if (role == 1){
            return PermissionEnum.USER;
        }
        else
            return PermissionEnum.ADMIN;
    }

}
