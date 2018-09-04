package com.shoppay.numc.http;

import java.util.List;

/**
 * Created by songxiaotao on 2018/7/5.
 */

public interface PermissionListener {
    void granted();
    void denied(List<String> deniedList);
}
