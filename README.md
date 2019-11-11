## 大案定损SDK简述
本SDK开发旨在提供方便快捷地对大案损失进行金额和配件的确认，通过VIN码进行车辆定型后即可使用圈选。

本SDK使用时需要获取正式授权的license文件。商务合作请联系[明觉科技](http://www.dataenlighten.com)，SDK仅提供合作客户使用，违用必究!
    
### 更新记录
|时间|版本|更新内容|
|----|-----|-----|
|2019年11月11日|0.6.4|1.定损定损结果改为回调得方式，<br>2.添加定损流程中将用户已选配件回传给APP|

## **使用步骤：**
### 0.拷贝申请到的mj_license.lic文件到assets目录中
### 1.添加依赖及权限：
在工程build.gradle配置脚本中buildscript和allprojects段中添加【明觉科技SDK】 新maven仓库地址
```java

allprojects {
    repositories {
        maven {
            url 'https://dl.bintray.com/dataenlighten/MJSeriousSDK'
        }
    }
}
```
#####     *Gradle
 在项目module build.gradle配置脚本中dependencies添加
```java
    //SDK依赖
    implementation 'com.dataenlighten.serious:mj_serious_ui:0.6.4'
    
```

#####     * 在项目AndroidManifest.xml配置脚本中添加权限
```java

    <!-- 网络状态 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!--获取网络状态，请求网络前判断是否有网络-->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
  
```
#####     * 在项目AndroidManifest.xml配置脚本中添加权限
```java
    #保留泛型。
    -keepattributes Signature
    #保留异常
    -keepattributes Exceptions
    #保留内部类
    -keepattributes InnerClasses
    #保留枚举
    -keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
    }
    
    #-libraryjars libs/okhttp3
    -dontwarn okhttp3.**
    -keep class okhttp3.** { *; }
    
    #-libraryjars libs/okio
    -dontwarn okio.**
    -keep class okio.** { *; }
    #Parcelable
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }
    
    ##Glide
    -dontwarn com.bumptech.glide.**
    -keep class com.bumptech.glide.**{*;}
    -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public class * extends com.bumptech.glide.AppGlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
    }
    
    
    # Gson specific classes
    -keep class sun.misc.Unsafe {*;}
    -keep class com.google.gson.stream.** {*;}
    # Application classes that will be serialized/deserialized over Gson
    -keep class com.google.gson.** {*;}
    # 注解
    -keepattributes *Annotation*
    # 反射
    -keepattributes EnclosingMethod
```

## **功能集成：**

###  1.初始化SDK(必须)
```java
    /**
     * 服务初始化（非主线程）
     *
     * @param ctx               依赖
     * @param userIdentifier    用户唯一标识(调用方系统中用户唯一标识)
     * @param onSdkInitListener 初始化回调（AbstractSDKInitCallback可切换第三线程和主线程的回调）
     * @throws LicenseNotFoundException 未发现license异常
     */
    void init(Context ctx, String userIdentifier, OnSdkInitListener onSdkInitListener) throws LicenseNotFoundException;
```
###### 示例
````java
try {
            MJSDKUIService.getInstance().init(getApplication(), "userIdentifier", new AbstractSDKInitCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String code, Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (LicenseNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "请检查授权文件", Toast.LENGTH_SHORT).show();
        }
````
###   2.VIN定型（定损前调用，确认车辆信息）
```java
    /**
     * VIN解析，返回对应VIN码的车辆信息
     * 当onSuccess回调List<MJVehicleObj>不唯一的时候需要用户根据optionInfo描述手动选择不同的optionCode
     * @param sessionCode   案件唯一标识，为空或者null的时候，SDK内部生成
     * @param vin           VIN码
     * @param queryCallBack 请求回调。
     */
    void VINQuery(String sessionCode, String vin, QueryCallBack queryCallBack);
```
###### 示例
````java
MJSDKUIService.getInstance().VINQuery("sessionCode", "LFV5A24G1G3000628", new AbstractQueryListBeanCallback<MJVehicleObj>(true) {
            @Override
            public void onFail(String code, Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<MJVehicleObj> resultList) {
            }
        });
````
### 3.初始化案件信息（调用VINQuery成功后设置车辆信息，在开始定损前调用）
```java
    /**
     * 初始化赔案信息
     *
     * @param vinCode    VINCode
     * @param carBody    VINQuery返回的CarInfo.getBody()
     * @param optionCode VINQuery返回的CarInfo.getOptionCode()
     */
    void setCarInfo(String vinCode, String carBody, String optionCode);
```
###### 示例
````java
MJSDKUIService.getInstance().setCarInfo(resultList.get(0).getVinCode(), resultList.get(0).getBody(), resultList.get(0).getOptionCode());
````
### 4.开始定损
```java
    /**
     * 开始定损
     *
     * @param ctx                 依赖
     * @param sdkUIDamageListener 回调
     */
    void startDamage(Context ctx, OnSdkUIDamageListener sdkUIDamageListener);
```
###### 示例
````java
MJSDKUIService.getInstance().startDamage(MainActivity.this, new OnSdkUIDamageListener() {
            @Override
            public void onDamageSuccess(PreviewOrderObj previewOrderObj) {
                Toast.makeText(MainActivity.this, previewOrderObj.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDamageFailure(Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
              @Override
            public void onSelectedKeyParts(ArrayList<Part> partArrayList) {
                Toast.makeText(MainActivity.this, "回传核心配件数量=="+partArrayList.size(), Toast.LENGTH_SHORT).show();
            }
        });
````


### 5.涉及字段说明
#####    MJVehicleObj（车辆信息）
| 字段名              | 类型    | 说明                                                                      |
| ------------------- | ------ |------------------------------------------------------------------------- |
| mjVehicleSys | String  | 车型（例如：雅阁）                                                       |
| year             | String   | 年款                                                             |
| prefix             | String   | 品牌前缀（Bmw）                                                             |
| gyroBrand             | String   |                                                              |
| carCountry        | String     | 国别         |
| vehicleEn          | String  | 车型英文名         |
| body | String  | 车体信息（三厢4门，MPV，SUV，两厢5门）                                                     |
| vehicleChn          | String  | 车型中文名         |
| vinCode | String  | 提交定型的vin                                                       |
| transmission           | String | 变速器(CVT)                                                        |
| carGrade             | String   | 级别（中大型、微型、中大suv）            |
| subBrand       | String | 子品牌（一汽大众） |
| engine       | String |  |
| optionCode       | String |车型配置编号 |
| optionInfo       | String   | 车型配置信息 |
| displacement        | String     | 排量                                                         |
| mjVehicleGroup       | String   | 车型分组（例如：雅阁8代）|
| priceZone       | String   | |
| drive       | String   | 驱动形式(前轮驱动)|
| brand          | String  | 品牌    |
| minPrice | String  | 最低价格                           |
| maxPrice       | String   | 最高价格|

#####    QuoteInfo（损失详情）
| 字段名              | 类型   | 说明                                                                      |
| ------------------- | ------ |------------- |
| collisionCodeList   | list\<string\> | 碰撞信息编码    |
| partList            | list\<Part\>   | 核心配件清单    |
| quotePartInfos       |  list\<PartInfo\>| 配件更换列表数据  |
| quoteLaborInfos       | list\<OperationInfo\>| 工时项目列表数据 |
| ecmPartList            | list\<Part\>   | 博车网核心配件清单    |

##### CollisionInfo(碰撞信息)

| 字段名              | 类型    | 说明                 |
| ------------------- | ------ |------------- |
| collisionCode | String  | 明觉碰撞类型编码                                               |
| description       | String   | 明觉碰撞类型名称描述|

##### Part（核心配件配件）

| 字段名              | 类型    | 说明                                                                      |
| ------------------- | ------ |------------- |
| partCode | String  | 配件名称编号                                                      |
| stdPartName       | String   | 明觉标准名|
| partNumber       | String | 原厂名 |
| operation           | String | 工项 |
| operationId        | String     | 工项id                                               |

### quotePartInfos

| 字段名              | 类型   | 说明                                                                      |
| ------------------- | ------ |------------- |
| partNumber| String| 配件OE号                                                       |
| stdPartName       | String| 配件标准名称  |
| partCode| String  | 配件CODE                                                       |
| partPrice       | String | 配件金额 |
| partCount      | int | 配件数量 |
| parent      | List<String>| 总成 |
| child      | List<String> | 局部 |
| partFrom| String| 配件来源(partRecommend-圈选和二次推荐，queryPartByImg-图查配件，voice-语音，word-文字，initial-首字母，custom-自定义,,recommend-推荐) |
| partType| String| 配件类型（little：小件、normal：标准件）                                                             |
| sysPartPrice| String| 系统配件价格                                                     |
| partMaterial| String|  配件材质                                                            |
| partAttribute| String| 配件属性                                                    |
| partQuality| String| 配件品质                                                           |
| supplierPrice| String| 直供价格                                                     |
| repairShopPrice| String|  修理厂报价                                                             |
| recommendSupplier| String| 推荐供应商                                                     |
| orderFlag| Boolean| 是否订购                                                           |
| recoveryType| String| 回收类型（single，batch，no）                                                    |
| residualValue| String| 残值                                                          |
| selfPaymentPct| String| 自付比例                                                  |
| transportationExpenses| String| 运费                                                 |


### quoteLaborInfos

| 字段名              | 类型   | 说明                                                                      |
| ------------------- | ------ | ------------ |
| stdPartName       | String| 配件标准名称  |
| partCode| String  | 配件CODE                                                       |
| operation       | String | 与中台交互的实际工项（例如: replace、panel、fit、accessoryFit、paint, material、externalRepair、electrical）【更换、钣金/维修、拆装、附件拆装、喷漆、辅料、外修、机电】 |
| laborCost      | String| 工项价格 |
| sysLaborCost       | String   | 系统工时金额|
| laborHour      | String| 工时数量 |
| severity      | String| 维修程度 light.轻 middle.中 serious.重|
| condition      | String| 喷漆类型（new新件/repair维修） |
| material     | String| 维修专用，材质：plastic塑料，iron铁质，aluminum铝质 |
| selectedFrom      | String| 工项来源（ custom-自定义，default-默认，recommend-推荐） |
| operationId| String| 工项代码 |
| count| Integer| 工项数量 |
| partNumber| String| OE |

### 6.其他错误码表

| 错误码 | 描述                   |
| ------ | ------------------ |
| 0000   | 成功                    |
| 1001   | VIN不合法               |
| 1002   | VIN不支持               |
| 1003   | 非乘用车                |
| 1004   | VIN错误                 |
| 1005   | VIN无法解析             |
| 1006   | 无权查看该品牌数据      |
| 1011   | 请求VIN的配件数据不存在 |
| 1012   | 该车无此配件            |
| 1013   | 请求图片不存在          |
| 1014   | 推荐配件数据不存在      |
| 1015   | 该车型查不到配件        |
| 9007   | 后台API异常             |
|8000002|	系统验证：成功退出系统！|
|0000|	系统验证：成功刷新访问票据！|
|8000004|	系统验证: 账号不存在或无权限!|
|8000005|	系统验证：需要进一步鉴权.|
|9999999|	其他错误，请联系技术支持！|
|9000001|	系统验证：请求参数不完整或者有错误！|
|9002|    系统验证：访问票据非法或已过期！|
|9000004|	系统验证：权限不足，无权访问该产品.|
|9000005|	系统验证：权限不足，无权访问该资源.|
|9006|	系统验证：认证失败！|
|9000007|	系统验证：请求验证码过于频繁 请稍后重试.|
|9000008|	系统验证：访问过于频繁 请稍后重试.|
|9000009|	系统验证：权限不足.|
|9000010|	服务响应超时 请稍后重试.|
|9000011|	请输入用户组名称|
|9000012|	外键错误|
|9000013|	手机号码格式错误|
|9000014|	列名部分有问题，请再次确认|
|9000015|	手机号已注册|
|9000024|	用户不存在|
|9000016|	用户名已注册|
|9000017|	分组已存在|
|9000018|	分组公司关系已存在|
|9000019|	请输入分组ID|
|9000020|	请选择分组|
|9000021|	请选择公司|
|9000022|	组织信息未填写，请确认|
|9005|	服务器升级中!|
