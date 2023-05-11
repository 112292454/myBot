package simbot.yzg.bot.commonapi.entity;

import lombok.Data;

@Data
public class PictureResponse {
    private byte[] content;

    private int id;

    private String title;

    private String description;

    private String type;

    private String info;
}
