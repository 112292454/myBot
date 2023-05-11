package simbot.yzg.bot.commonapi.entity;

import lombok.Data;


@Data
public class PicPath {

	private Integer id;
	private String path;
	private String kind;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}
