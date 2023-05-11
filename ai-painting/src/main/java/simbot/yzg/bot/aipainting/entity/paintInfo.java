package simbot.yzg.bot.aipainting.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class paintInfo implements Serializable {
//        public Long serialVersionUID = -7682789189752781789L;

	private String prompt;
	@JSONField(name = "all_prompts")
	private List<String> allPrompts;
	@JSONField(name = "negative_prompt")
	private String negativePrompt;
	@JSONField(name = "all_negative_prompts")
	private List<String> allNegativePrompts;
	private long seed;
	@JSONField(name = "all_seeds")
	private List<Long> allSeeds;
	private long subseed;
	@JSONField(name = "all_subseeds")
	private List<Long> allSubseeds;
	@JSONField(name = "subseed_strength")
	private double subseedStrength;
	private int width;
	private int height;
	@JSONField(name = "sampler_name")
	private String samplerName;
	@JSONField(name = "cfg_scale")
	private double cfgScale;
	private int steps;
	@JSONField(name = "batch_size")
	private int batchSize;
	@JSONField(name = "restore_faces")
	private boolean restoreFaces;
	@JSONField(name = "face_restoration_model")
	private String faceRestorationModel;
	@JSONField(name = "sd_model_hash")
	private String sdModelHash;
	@JSONField(name = "seed_resize_from_w")
	private int seedResizeFromW;
	@JSONField(name = "seed_resize_from_h")
	private int seedResizeFromH;
	@JSONField(name = "denoising_strength")
	private double denoisingStrength;
//	@JSONField(name = "extra_generation_params")
//	private Object extraGenerationParams;
	@JSONField(name = "index_of_first_image")
	private int indexOfFirstImage;
	private List<String> infotexts;
	private List<String> styles;
	@JSONField(name = "job_timestamp")
	private String jobTimestamp;
	@JSONField(name = "clip_skip")
	private int clipSkip;
	@JSONField(name = "is_using_inpainting_conditioning")
	private boolean isUsingInpaintingConditioning;
	private long usedTime;

	public String myString() {
		return "info:" +
				"prompt='" + prompt.replace("masterpiece, best quality, illustration, (dramatic shadows), (detailed face:1.2),  (higher) , 2k , an extremely delicate and beautiful girl,(ultra-detailed:1.05),", "") + '\n' +
				"seed=" + seed +
				", used time=" + usedTime / 1000 + "s" +
				", size=" + width + "*" + height;
	}
}
