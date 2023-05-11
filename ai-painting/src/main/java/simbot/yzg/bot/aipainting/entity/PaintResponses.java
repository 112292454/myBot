package simbot.yzg.bot.aipainting.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Auto-generated: 2023-04-29 22:7:23
 *
 * @author www.jsons.cn
 * @website http://www.jsons.cn/json2java/
 */
@Data
public class PaintResponses implements Serializable {

//    public Long serialVersionUID = -7682787979281252789L;

    private List<String> images;
    private t2iArgs parameters;
    //TODO:用paintInfo会报错
    @JSONField(deserialize = false,serialize = false)
    private paintInfo info;

    @JSONField(name = "info")
    private String temp;


}
