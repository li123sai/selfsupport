$().ready(function() {


    /*查询余额*/
    $('.btn-secondary').click(function(){
        queryOrder();
    })





/*获取gas*/
function queryOrder(){
	var deliveryId =$("#psbs").val();
	var mtPeisongId =$("#mtnbdh").val();
    if(deliveryId=="" || deliveryId==null){
        alert("配送标识！");
        return false;
    }
    if(mtPeisongId=="" || mtPeisongId==null){
        alert("美团内部单号！");
        return false;
    }
    $.ajax({
        url:"/mt/query/orderSatus",
		data:{"deliveryId":deliveryId,"mtPeisongId":mtPeisongId},
        type:"POST",
        //async:false,
        success:function(data){
            if(data.success){
                alert("成功");
            }else{
                alert("失败");
            }
        }
    });
}








});