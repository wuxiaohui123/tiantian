/**
 * 流程引擎的url配置
 */
var KISBPM = KISBPM || {};

KISBPM.URL = {
    getModel: function(modelId) {
        return ACTIVITI.CONFIG.contextRoot + 'abpmn/modelEditorJsonAction!getEditorJson.do?modelId=' + modelId;
    },

    getStencilSet: function() {
        return ACTIVITI.CONFIG.contextRoot + 'abpmn/stencilsetRestResourceAction!getStencilset.do?version=' + Date.now();
    },

    putModel: function(modelId) {
        return ACTIVITI.CONFIG.contextRoot + 'abpmn/modelSaveJsonAction!saveProcessModel.do?modelId=' + modelId;
    },
    
    getProcessRoleData: function(){
    	return ACTIVITI.CONFIG.contextRoot + 'abpmn/processAssignmentAction!getProcessRoleData.do';
    },
    
    getProcessUserData: function(){
    	return ACTIVITI.CONFIG.contextRoot + 'abpmn/processAssignmentAction!getProcessUserData.do';
    }
};