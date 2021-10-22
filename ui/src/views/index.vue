<template>
  <div class="app-container home" ref="xxxxxxxxxx">
    <el-row>
      <el-col v-for="(item,index) in tenantList" :key="index">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <el-button style="padding:1px;width: 100%;height: 100%" @click="targeUrl(getUrl(item.tenantUrl))">
                <template v-if="item.tenantImg!='' && item.tenantImg!=null">
                  <img name="tenantImg" :src="item.tenantImg" style="box-sizing: border-box" width="100%"/>
                </template>
                <template v-else>
                  <template v-if="isIfream()">
                    {{getIframe(index,item,getUrl(item.tenantUrl))}}
                  </template>
                  <img name="tenantImg" style="box-sizing: border-box" width="100%"/>
                </template>
            </el-button>
          </div>
          <div class="text item">
          <el-row>
            <el-col>
              <span>{{item.tenantName}}</span>
              <span style="float: right; padding: 3px 0">
              <el-button type="text" @click="clearTenantImg(index,item,getUrl(item.tenantUrl))">重置预览图</el-button>
            </span>
            </el-col>
            <el-col>{{item.remark}}</el-col>
          </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div id="ifremBox" style="width: 0px;height: 0px;overflow: hidden"></div>
  </div>
</template>

<script>
import {myTenant, updateTenant} from "@/api/system/tenant";
import html2canvas from "html2canvas"
import request from "@/utils/request";

export default {
  name: "index",
  data() {
    return {
      reTenantImg:true,
      loading:true,
      queryParams:{},
      tenantList:[],
      total:0
    };
  },
  created() {
    this.getList();
  },
  beforeMount() {
    if(this.isIfream()) {
      setTimeout(()=>{
        var form = document.getElementsByTagName('body')[0];
        console.log(form);
        html2canvas(form, {
          useCORS: true,
          logging: true,
          width:1920,
          height: 1080,
          windowWidth: 1920,
          windowHeight: 1080,
          x: 0,
          y: window.pageYOffset,
        }).then(canvas => {
          // 转成图片，生成图片地址
          var dataURL = canvas.toDataURL("image/png");
          window.parent.postMessage({url:document.location.origin,msg:dataURL},'*');
        })
      },500)
    }
  },
  methods: {
    getList() {
      this.loading = true;
      myTenant(this.queryParams).then(response => {
        this.tenantList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    clearTenantImg(index,item,url){
      if(this.reTenantImg){
        this.tenantList[index].tenantImg="";
        document.getElementsByName("tenantImg")[index].src="";
        updateTenant({tenantId:item.tenantId,tenantImg:""})
        this.getIframe(index,item,url);
      }else{
        this.msgError("请等待其他预览图生成");
        setTimeout(()=>{this.reTenantImg=true;},5000);
      }
    },
    getIframe(index,item,url){
      let iframe = document.createElement('iframe');
      iframe.src=url;
      window.onmessage=(event)=>{
        console.log(event);
        if(event.data.url==url){
          document.getElementsByName("tenantImg")[index].src=event.data.msg;
          updateTenant({tenantId:item.tenantId,tenantImg:event.data.msg})
          window.onmessage=null;
          iframe.remove();
          this.reTenantImg=true;
        }
      }
      document.getElementById("ifremBox").appendChild(iframe);
      this.reTenantImg=false;
    },
    getUrl(route){
      var strings = window.location.origin.split(".");
      if(route!=null && route!=""){
        if(strings[0].indexOf("www")>=0){
          strings[1]=route+"."+strings[1];
        }else if(isNaN(strings[0])){
          strings=["http://www",route,"xxxxxxxx.com"];
        }else if(strings[0].indexOf("localhost")>=0){
          strings=["http://www",route,"xxxxxxxx.com"];
        }
      }
      return strings.join(".");
    },
    isIfream(){
      return self!=top
    },
    targeUrl(url){
      window.open(url,"_blank")
    }
  },
};
</script>

<style scoped lang="scss">
.clearfix{
  font-weight: bold;
  height: 500px;
  overflow:hidden;
  background-image: linear-gradient(#fff, #ccc);
}
.box-card{
  margin-bottom: 20px;
}
</style>

