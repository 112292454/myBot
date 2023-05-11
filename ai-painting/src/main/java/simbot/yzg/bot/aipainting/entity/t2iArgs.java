package simbot.yzg.bot.aipainting.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import simbot.yzg.bot.commonapi.entity.Constants;

import java.io.Serializable;
import java.util.*;
/**
 * Auto-generated: 2023-04-29 18:54:18
 *
 * @author www.jsons.cn
 * @website http://www.jsons.cn/json2java/
 */
@Data
@JsonSerialize
@JsonDeserialize
public class t2iArgs implements Serializable {

    private static final long serialVersionUID = 86434898514891584L;


    private static final ObjectMapper objmapper = new ObjectMapper();
    private static final Map<Double,Integer> autoShapes;
    static {
        int k=10000;
        HashMap<Double,Integer> temp=new HashMap<>();
        temp.put(500.0,512*k+768);
        temp.put(100.0,384*k+832);
        temp.put(300.0,512*k+512);

//        temp.put(80.0,768*k+768);
        temp.put(200.1,384*k+768);
//        temp.put(70.0,512*k+832);
        temp.put(50.1,832*k+384);
        temp.put(80.2,768*k+512);
        autoShapes=adjustProbability(temp);

    }

    @JSONField(name="enable_hr")
    private boolean enableHr;
    @JSONField(name="denoising_strength")
    private double denoisingStrength;
//    @JSONField(name="firstphase_width")
//    private int firstphaseWidth;
//    @JSONField(name="firstphase_height")
//    private int firstphaseHeight;
    @JSONField(name="hr_scale")
    private double hrScale;
    @JSONField(name="hr_upscaler")
    private String hrUpscaler;
    @JSONField(name="hr_second_pass_steps")
    private int hrSecondPassSteps;
//    @JSONField(name="hr_resize_x")
//    private int hrResizeX;
//    @JSONField(name="hr_resize_y")
//    private int hrResizeY;
    private String prompt;
    private List<String> styles;
    private int seed;
//    private int subseed;
//    @JSONField(name="subseed_strength")
//    private double subseedStrength;
//    @JSONField(name="seed_resize_from_h")
//    private int seedResizeFromH;
//    @JSONField(name="seed_resize_from_w")
//    private int seedResizeFromW;
    @JSONField(name="sampler_name")
    private String samplerName;
    @JSONField(name="batch_size")
    private int batchSize;
    @JSONField(name="n_iter")
    private int nIter;
    private int steps;
    @JSONField(name="cfg_scale")
    private double cfgScale;
    private int width;
    private int height;
    @JSONField(name="restore_faces")
    private boolean restoreFaces;
    private boolean tiling;
    @JSONField(name="do_not_save_samples")
    private boolean doNotSaveSamples;
    @JSONField(name="do_not_save_grid")
    private boolean doNotSaveGrid;
    @JSONField(name="negative_prompt")
    private String negativePrompt;
//    private double eta;
//    @JSONField(name="s_churn")
//    private double sChurn;
//    @JSONField(name="s_tmax")
//    private double sTmax;
//    @JSONField(name="s_tmin")
//    private double sTmin;
//    @JSONField(name="s_noise")
//    private double sNoise;
    @JSONField(name="override_settings")
    private Object overrideSettings;
    @JSONField(name="override_settings_restore_afterwards")
    private boolean overrideSettingsRestoreAfterwards;
    @JSONField(name="script_args")
    private List<String> scriptArgs;
    @JSONField(name="sampler_index")
    private String samplerIndex;
    @JSONField(name="script_name")
    private String scriptName;
    @JSONField(name="send_images")
    private boolean sendImages;
    @JSONField(name="save_images")
    private boolean saveImages;
    @JSONField(name="alwayson_scripts")
    private Object alwaysonScripts;

    private transient boolean needShape=true;



    public t2iArgs()  {

    }

    public t2iArgs(String prompt) {
        init();
        this.prompt = prompt;
    }

    public t2iArgs(String prompt, String style) {
        this(prompt);
        this.styles.clear();
        this.styles.add(style);
    }

    public t2iArgs(String prompt, int width, int height){
        this(prompt);
        setSize(width, height);
    }

    public void setNum(int batchCount, int batchSize){
        this.nIter=batchCount;
        this.batchSize=batchSize;
    }

    public void setSize(int width, int height){
        this.width= (int) (Math.round(width/64.0)*64);
        this.height= (int) (Math.round(height/64.0)*64);
    }

    public void addPrompt(String prompt) {
        this.prompt = Constants.DEFAULT_PROMPT_PREFIX+"("+prompt+":1.2)";
    }

    public void setEnableHr(boolean enableHr) {
        this.steps*=0.7;
        this.enableHr = enableHr;
    }

    public double getCostSeconds() {
        double time = 7.0;

        time *= this.width / 512.0;
        time *= this.height / 512.0;
        if(time>30) return -1;

        time *= this.steps / 20.0;

        time *= this.enableHr ? 4 : 1;
        time *= this.enableHr ? this.hrSecondPassSteps / 17.0 : 1;

        time *= this.nIter;
        time *= this.batchSize;


        return time;
    }

    public boolean costCanPass() {
        double v = getCostSeconds();
        return v>0&&v<100;
    }

    public t2iArgs initArgs(t2iArgs args) {
        this.enableHr = args.enableHr;
        this.denoisingStrength = args.denoisingStrength;
//        this.firstphaseWidth = args.firstphaseWidth;
//        this.firstphaseHeight = args.firstphaseHeight;
        this.hrScale = args.hrScale;
        this.hrUpscaler = args.hrUpscaler;
        this.hrSecondPassSteps = args.hrSecondPassSteps;
//        this.hrResizeX = args.hrResizeX;
//        this.hrResizeY = args.hrResizeY;
        this.prompt = args.prompt;
        this.styles = args.styles;
        this.seed = args.seed;
//        this.subseed = args.subseed;
//        this.subseedStrength = args.subseedStrength;
//        this.seedResizeFromH = args.seedResizeFromH;
//        this.seedResizeFromW = args.seedResizeFromW;
        this.samplerName = args.samplerName;
        this.batchSize = args.batchSize;
        this.nIter = args.nIter;
        this.steps = args.steps;
        this.cfgScale = args.cfgScale;
        this.width = args.width;
        this.height = args.height;
        this.restoreFaces = args.restoreFaces;
        this.tiling = args.tiling;
        this.doNotSaveSamples = args.doNotSaveSamples;
        this.doNotSaveGrid = args.doNotSaveGrid;
        this.negativePrompt = args.negativePrompt;
//        this.eta = args.eta;
//        this.sChurn = args.sChurn;
//        this.sTmax = args.sTmax;
//        this.sTmin = args.sTmin;
//        this.sNoise = args.sNoise;
//        this.overrideSettings = args.overrideSettings;
        this.overrideSettingsRestoreAfterwards = args.overrideSettingsRestoreAfterwards;
        this.scriptArgs = args.scriptArgs;
        this.samplerIndex = args.samplerIndex;
        this.scriptName = args.scriptName;
        this.sendImages = args.sendImages;
        this.saveImages = args.saveImages;
        this.alwaysonScripts = args.alwaysonScripts;
        this.needShape=args.needShape;
        return this;
    }

    public void autoShape(){
        double cnt=0;
        Double k=new Random().nextDouble();
        for (Map.Entry<Double, Integer> entry : autoShapes.entrySet()) {
            cnt+=entry.getKey();
            if(k<cnt) {
                setSize(entry.getValue()/10000, entry.getValue()%10000);
                break;
            }
        }
    }


    public static <V> Map<Double,V> adjustProbability(Map<Double, V> source){
        HashMap<Double, V> res=new HashMap<>(source.size());
        List<Map.Entry<Double, V>> entryList =new ArrayList<>();
        double sum=0;
        for (Map.Entry<Double, V> entry : source.entrySet()) {
            sum += entry.getKey();
            entryList.add(entry);
        }
        entryList.sort(Comparator.comparingDouble(Map.Entry::getKey));
        double finalSum = sum;
        entryList.forEach(a->res.put(a.getKey()/ finalSum, a.getValue()));
        return res;
    }

    public t2iArgs init(){
        initArgs(JSONObject.parseObject(Constants.DEFAULT_ARGS, t2iArgs.class));
        autoShape();
        return this;
    }


}