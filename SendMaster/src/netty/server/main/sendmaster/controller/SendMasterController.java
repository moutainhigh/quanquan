package netty.server.main.sendmaster.controller;

import io.netty.handler.codec.http.FullHttpResponse;

import java.util.List;

import netty.server.BaseControl;
import netty.server.ErrorCode;
import netty.server.HttpBack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.sm.main.TaskThreadPool;
import com.sm.service.ManageTask;
import com.sm.service.ZookeeperService;



/**
 * @author zhoujia
 *
 * @date 2015年7月27日
 */
@Controller("/sendmaster")
@Scope("prototype")
public class SendMasterController extends BaseControl {
	//AtomicInteger count = new AtomicInteger(0);

	private static Logger logger = LoggerFactory.getLogger(SendMasterController.class);
	ZookeeperService zkService = new ZookeeperService();
    public FullHttpResponse start(){
    	String param = strings("param");
    	List<String> slaveNodes = zkService.getAllSlaveNodes();
    	if(slaveNodes == null || slaveNodes.size()==0){
    		logger.error("start 方法异常，集群子节点数量为0，请检查集群状态！");
    		HttpBack.back_500(ctx);
			return HttpBack.back_error(ctx, ErrorCode.NO_NODE_ERROR);
    	}
    	ManageTask mtk = new ManageTask();
    	
    	List<String> availableSlave = mtk.availableSlave;
    	List<String> questionAvailableSlave = mtk.questionSlave;

    	if(param != null){
    		String[] split = param.split(",");
    		if(split[1].equals("fati.jar")){//发题   
    			mtk.initQuestionSlaveNode();
    			if(questionAvailableSlave.size() == 0){ // 如果发题可用节点为空，返回异常
    				return HttpBack.back_error(ctx,ErrorCode.NO_AVAILABLE_ERROR);
    			}
    			logger.info("发题可用节点:" + questionAvailableSlave);
    		}else{//普通任务
    			mtk.initCommonSlaveNode();
    			if(availableSlave.size()==0){
    				return HttpBack.back_error(ctx,ErrorCode.NO_AVAILABLE_ERROR); // 如果其他任务可用节点为空， 返回异常
    			}
    			logger.info("普通任务可用节点:"+availableSlave);
    		}
    	}
    	
		try {
			//int threadCount = Math.abs(count.incrementAndGet()%5);//大于最大值后溢出为负数，所以取绝对值
			logger.info("程序入口，参数:" + param);
			//TaskThreadPool.taskabq[threadCount%5].put(param);
			TaskThreadPool.runThread(param);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("start 方法异常，参数：" + param + "\n",e);
			HttpBack.back_500(ctx);
			return HttpBack.back_error(ctx,ErrorCode.SYS_ERR);
		}
        
        HttpBack.back_200(ctx);
        return HttpBack.back_error(ctx,ErrorCode.OK);
    }
}
