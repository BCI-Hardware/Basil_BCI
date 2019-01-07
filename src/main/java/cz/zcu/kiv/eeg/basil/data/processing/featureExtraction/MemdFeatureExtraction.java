package cz.zcu.kiv.eeg.basil.data.processing.featureExtraction;

import cz.zcu.kiv.eeg.basil.data.processing.structures.EEGDataPackage;
import hht.DecompositionRunner;
import hht.HhtSimpleRunner;
import hht.memd.MultivariateEMD;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tomas Prokop on 20.02.2018.
 */
public class MemdFeatureExtraction implements IFeatureExtraction {

    private DecompositionRunner decompositionRunner = new DecompositionRunner();

    public void setMaxImfs(int max){
        decompositionRunner.setMaxImfs(max);
    }

    public int getMaxImfs(){
        return decompositionRunner.getMaxImfs();
    }

    public MemdFeatureExtraction(int maxImfs){
        setMaxImfs(maxImfs);
    }

    private MultivariateEMD memd = null;

    public MemdFeatureExtraction(){
        //memd = decompositionRunner.loadFromCfg(null);
    }

    @Override
    public FeatureVector extractFeatures(EEGDataPackage data) {
        if(data == null || data.getData() == null) {
            throw new IllegalArgumentException("data are null");
        }

        if(memd == null) {
            memd = decompositionRunner.loadFromCfg(null);
        }

        try {
            memd.getImfs().clear();
            List<double[][]> features = decompositionRunner.runMemd(memd,data.getData());
            double[][][] ff = features.toArray(new double[features.size()][][]);
            double[] flat = ArrayUtil.flattenDoubleArray(ff);
            int[] shape = {ff.length, ff[0].length, ff[0][0].length};
            INDArray arr = Nd4j.create(flat, shape);

            return new FeatureVector(arr);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO handle
        }

        return null;
    }

    @Override
    public int getFeatureDimension() {
        return 0;
    }
}
