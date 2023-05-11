package simbot.yzg.bot.commonapi.entity;

import lombok.Data;

@Data
public class Constants {
	public  static final String DEFAULT_ARGS="{\n" +
			"  \"enable_hr\": false,\n" +
			"  \"denoising_strength\": 0.5,\n" +
			"  \"firstphase_width\": 0,\n" +
			"  \"firstphase_height\": 0,\n" +
			"  \"hr_scale\": 1.7,\n" +
			"  \"hr_upscaler\": \"Latent (nearest-exact)\",\n" +
			"  \"hr_second_pass_steps\": 17,\n" +
			"  \"hr_resize_x\": 0,\n" +
			"  \"hr_resize_y\": 0,\n" +
			"  \"prompt\": \"masterpiece, best quality, illustration, (dramatic shadows), (detailed face:1.2), (higher) , 2k ,__pos__,__chara__,__cloth__,__lora__,\",\n" +
			"  \"styles\": [\n" +
			"    \"\"\n" +
			"  ],\n" +
			"  \"seed\": -1,\n" +
			"  \"subseed\": -1,\n" +
			"  \"subseed_strength\": 0,\n" +
			"  \"seed_resize_from_h\": -1,\n" +
			"  \"seed_resize_from_w\": -1,\n" +
			"  \"sampler_name\": \"DPM++ SDE Karras\",\n" +
			"  \"batch_size\": 1,\n" +
			"  \"n_iter\": 1,\n" +
			"  \"steps\": 20,\n" +
			"  \"cfg_scale\": 5,\n" +
			"  \"width\": 256,\n" +
			"  \"height\": 256,\n" +
			"  \"restore_faces\": false,\n" +
			"  \"tiling\": false,\n" +
			"  \"do_not_save_samples\": false,\n" +
			"  \"do_not_save_grid\": false,\n" +
			"  \"negative_prompt\": \"(worst quality, low quality:1.5)\"" +
			"  \"eta\": 0,\n" +
			"  \"s_churn\": 0,\n" +
			"  \"s_tmax\": 0,\n" +
			"  \"s_tmin\": 0,\n" +
			"  \"s_noise\": 1,\n" +
			"  \"override_settings\": {},\n" +
			"  \"override_settings_restore_afterwards\": false,\n" +
			"  \"script_args\": [],\n" +
			"  \"sampler_index\": \"DPM++ SDE Karras\",\n" +
			"  \"script_name\": \"\",\n" +
			"  \"send_images\": true,\n" +
			"  \"save_images\": true,\n" +
			"  \"alwayson_scripts\": {\n" +
			"\n" +
			"  }\n" +
			"}";

	public  static final String DEFAULT_PROMPT_PREFIX="masterpiece, best quality, illustration, (dramatic shadows), (detailed face:1.2),  (higher) , 2k , an extremely delicate and beautiful,(ultra-detailed:1.05) , (best quality:1.05) , (masterpiece:1.05) ,traditional_media , lineart ,graphite(medium) ,";

}
