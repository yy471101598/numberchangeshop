package com.shoppay.numc.nbean;

import java.io.Serializable;

/**
 * Created by songxiaotao on 2018/9/5.
 */

public class HomeMsg implements Serializable{
    public int TypeID;//;//类型1.固定(F)2.刷卡导航(P)3.网页(U)
    public String Title;//名称
    public String EnTitle;//英文名称
    public String Code;//模块代码
    public String Url;//网址
    public String Icon;//图标

}
