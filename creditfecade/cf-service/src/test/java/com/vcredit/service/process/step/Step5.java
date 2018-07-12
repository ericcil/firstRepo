package com.vcredit.service.process.step;

import com.vcredit.service.process.AfterStepCallback;
import com.vcredit.service.process.AssembleStep;
import com.vcredit.service.process.dto.DefaultChannelParam;
import com.vcredit.service.process.dto.ProcessContext;
import com.vcredit.service.process.dto.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试类
 * @Author chenyubin
 * @Date 2018/7/3
 */
public class Step5 extends AssembleStep<DefaultChannelParam> {
    Logger logger = LoggerFactory.getLogger(Step5.class);
    private final String key = Step5.class.getName();

    public Step5(AfterStepCallback callback) {
        super(callback);
    }

    @Override
    public void executeStep(ProcessContext<DefaultChannelParam> context) {
        logger.info("{}执行=====",key);
        StepResult s = new StepResult();
        s.setMsg(key+"执行完成,");
        context.setCurrentStepResult(s);
        context.setProcessInterrupt(false);
        DefaultChannelParam param = context.getProcessParam();
        param.setCount(param.getCount() + 1);
        try {
            Thread.sleep(500); //模拟耗时操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void fireCallback(ProcessContext<ChannelParam1> context, AfterStepCallback callback) {

    }*/

    @Override
    public String getStepName() {
        return key;
    }
}