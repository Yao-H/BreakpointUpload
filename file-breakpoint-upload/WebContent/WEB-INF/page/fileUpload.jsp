<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="description" content="Ajax文件上传断点续传" />
<meta name="description" content="前端学习实例页面 Ajax文件上传断点续传" />
<meta name="keywords" content="web前端, css, jQuery, javascript" />
<meta name="author" content="" />
<title>Ajax文件上传断点续传</title>
<link rel="stylesheet" href="../css/demo.css" type="text/css" />
<link rel="stylesheet" href="../css/btn.css" type="text/css" />
<link rel="stylesheet" href="../css/hl.css" type="text/css" />
<style>
ul { padding: 0; margin: 0; list-style-type: none; }
.upload { width: 600px; margin: 0 auto; }
.form { padding: 12px 0 30px; }
.file { position: absolute; width: 230px; height: 34px; cursor: pointer; opacity: 0; }
.file:hover + .btn-info { background-color: #39b3d7; border-color: #269abc; }
[type="submit"] { width: 100px; margin-left: 260px; float: right; visibility: hidden; }
.upload_ul { display: none; width: 100%; border: 1px solid #bbb; background-color: #fff; font-size: 12px; }
.upload_ul > li { display: table-row; opacity: 1; overflow: hidden; -webkit-transition: opacity .2s; transition: opacity .2s;
	background-image:-webkit-linear-gradient(top, #ABD9FF, #88C9FF);
	background-image:        linear-gradient(to bottom, #ABD9FF, #88C9FF);
	-moz-background-size: 0% 100%;
	background-size: 0% 100%;
	background-repeat: no-repeat;
}
.upload_title { color: #666; background-color: #f0f0f0; }
.upload_cell { display: table-cell; padding: 5px 10px; border-top: 1px solid #ddd; vertical-align:middle; }
.upload_cell:first-child { width: 50%; }
.waiting{ color: #999; }
.uploading { color: #069; }
.canceling { color: #CE4625; }
.success { color: green; }
.error { color: #f30; }
.remind { padding: 5px 10px; background-color: #eee; margin-top: 40px; color: #666; font-size: 12px;}

/*icon copyright by weiyun */
.icon { display: inline-block; width: 30px; height: 30px; background: url(http://www.zhangxinxu.com/study/201310/file-small.png) no-repeat -74px -490px; vertical-align: middle; }
.icon-doc,.icon-docx{background-position:0 0}
.icon-ppt,.icon-pptx{background-position:-37px 0}
.icon-xls,.icon-xlsx{background-position:-74px 0}
.icon-pdf,.ico-pdf{background-position:-111px 0}
.icon-txt,.ico-txt{background-position:-148px 0}
.icon-msg,.ico-msg{background-position:0 -35px}
.icon-rp,.ico-rp{background-position:-37px -35px}
.icon-vsd,.ico-vsd{background-position:-74px -35px}
.icon-ai,.ico-ai{background-position:-111px -35px}
.icon-eps,.ico-eps{background-position:-148px -35px}
.icon-log,.ico-log{background-position:0 -70px}
.icon-xmin,.ico-xmin{background-position:-37px -70px}
.icon-cab,.ico-cab{background-position:-74px -70px}
.icon-psd,.ico-psd{background-position:0 -105px}
.icon-jpg,.ico-jpg{background-position:-37px -105px}
.icon-jpeg,.ico-jpeg{background-position:-37px -105px}
.icon-png,.ico-png{background-position:-74px -105px}
.icon-gif,.ico-gif{background-position:-111px -105px}
.icon-bmp,.ico-bmp{background-position:-148px -105px}
.icon-rmvb,.ico-rmvb{background-position:0 -140px}
.icon-rm,.ico-rm{background-position:0 -140px}
.icon-mod,.ico-mod{background-position:-37px -140px}
.icon-mov,.ico-mov{background-position:-74px -140px}
.icon-3gp,.ico-3gp{background-position:-111px -140px}
.icon-avi,.ico-avi{background-position:-148px -140px}
.icon-swf,.ico-swf{background-position:0 -175px}
.icon-flv,.ico-flv{background-position:-37px -175px}
.icon-mpe,.ico-mpe{background-position:-74px -175px}
.icon-asf,.ico-asf{background-position:-111px -175px}
.icon-wmv,.ico-wmv{background-position:-148px -175px}
.icon-mp4,.ico-mp4{background-position:-185px -175px}
.icon-wma,.ico-wma{background-position:0 -210px}
.icon-mp3,.ico-mp3{background-position:-37px -210px}
.icon-wav,.ico-wav{background-position:-74px -210px}
.icon-apk,.ico-apk{background-position:0 -245px}
.icon-ipa,.ico-ipa{background-position:-37px -245px}
.icon-exe,.ico-exe{background-position:-74px -245px}
.icon-msi,.ico-msi{background-position:-111px -245px}
.icon-bat,.ico-bat{background-position:-148px -245px}
.icon-fla,.ico-fla{background-position:0 -280px}
.icon-htm,.ico-htm,.icon-html,.ico-html{background-position:-37px -280px}
.icon-c,.ico-c{background-position:-111px -280px}
.icon-xml,.ico-xml{background-position:-148px -280px}
.icon-asp,.ico-asp{background-position:-185px -280px}
.icon-chm,.ico-chm{background-position:0 -315px}
.icon-hlp,.ico-hlp{background-position:-37px -315px}
.icon-ttf,.ico-ttf{background-position:-111px -315px}
.icon-otf,.ico-otf{background-position:-148px -315px}
.icon-rar,.ico-rar{background-position:0 -350px}
.icon-zip,.ico-zip{background-position:-37px -350px}
.icon-tar,.ico-tar{background-position:-74px -350px}
.icon-cab,.ico-cab{background-position:-111px -350px}
.icon-uue,.ico-uue{background-position:-148px -350px}
.icon-jar,.ico-jar{background-position:0 -385px}
.icon-7z,.ico-7z{background-position:-37px -385px}
.icon-iso,.ico-dmg{background-position:-74px -385px}
.icon-dmg,.ico-dmg{background-position:-111px -385px}
.icon-ace,.ico-ace{background-position:-148px -385px}
.icon-bak,.ico-bak{background-position:0 -420px}
.icon-tmp,.ico-tmp{background-position:-37px -420px}
.icon-old,.ico-old{background-position:-74px -420px}
.icon-document,.ico-document{background-position:0 -455px}
.icon-exec,.ico-exec{background-position:-37px -455px}
.icon-code,.ico-code{background-position:-74px -455px}
.icon-image,.ico-image{background-position:-111px -455px}
.icon-video,.ico-video{background-position:-148px -455px}
.icon-audio,.ico-audio{background-position:0 -490px}
.icon-compress,.ico-compress{background-position:-37px -490px}
.icon-unknow,.ico-unknow{background-position:-74px -490px}
.icon-filebroken,.ico-filebroken{background-position:-111px -490px}
.icon-link,.ico-link{background-position:-111px -418px}
</style>
</head>

<body>
<div id="header">
	<a href="/" class="logo" title="回到鑫空间-鑫生活首页">
    	<img src="/php/image/zxx_home_logo.png" border="0" />
    </a>
    <div class="ad"><script type="text/javascript">	google_ad_client = "pub-0090627341039040";google_ad_slot = "2041257798";google_ad_width = 468;google_ad_height = 60;</script><script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
    <script type="text/javascript">
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-11205167-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
		var ga = document.createElement('script');
		 ga.type = 'text/javascript';
		 ga.async = true;
		ga.src = 'http://www.google-analytics.com/ga.js';
		var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	</script></div>
</div>
<div id="main">
	<h1>Ajax文件上传断点续传实例页面</h1>
    <div id="body" class="light">
    	<div id="content" class="show">
        	<h3>Ajax断点续传展示</h3>
            <div class="article_new"><a href="http://www.zhangxinxu.com/wordpress/?p=3754">回到相关文章 »</a></div>
            <div class="demo">
            	<div class="upload">
                    <form id="form" class="form" action="upload.php" enctype="multipart/form-data">
                        <input type="file" id="file" class="file" name="file[]" multiple><span class="btn btn-info">请选择要上传的文件(小于200K)</span>
                        <input type="submit" id="submit" class="btn btn-primary" value="上传">
                    </form>
                    <ul id="uploadUl" class="upload_ul">
                        <li class="upload_title">
                            <div class="upload_cell">标题</div>
                            <div class="upload_cell">类型</div>
                            <div class="upload_cell">大小</div>
                            <div class="upload_cell">状态</div>
                            <div class="upload_cell">操作</div>
                        </li>
                    </ul>
                    <p class="remind">1. 点击默认展示的按钮选择文件；<br>2. 点击后出现的上传按钮进行上传</p>
                </div>
            </div>
        </div>       
    </div>
</div>
<!-- 上传文件展示模板 -->
<script id="fileTemplate" type="text/template">
<li id="filelist_$id$">
	<div class="upload_cell">$name$</div>
	<div class="upload_cell"><i class="icon icon-$type$"></i></div>
	<div class="upload_cell">$size$</div>
	<div id="filestatus_$id$" class="upload_cell">$status$</div>
	<div id="fileoperate_$id$" class="upload_cell">$operate$</div>
</li>
</script>
<script>
var $ = function (id) {
	return document.getElementById(id)
};
var eleForm = $("form");
var eleFile = $("file");
var eleSubmit = $("submit");
var eleUploadUl = $("uploadUl");
var eleTemplate = $("fileTemplate");
var fileArray = [];
var fileSplitSize = 1024 * 100; // 切割文件块大小
var htmlTemplate = eleTemplate && eleTemplate.innerHTML || "";
if (typeof history.pushState == "function") {
	var objStateElement = (function () {
		var _$ = function (name, fileid) {
			return $("file" + name + "_" + fileid) || {
				innerHTML: ""
			}
		};
		return {
			backgroundSize: function (params, percent) {
				var dom = typeof params == "string" ? $("filelist_" + params) : params;
				if (dom) {
					dom.style.mozBackgroudSize = percent + "% 100%";
					dom.style.backgroundSize = percent + "% 100%"
				}
			},
			wait: function (fileid) {
				_$("status", fileid).innerHTML = '<span class="uploading">上传中...</span>';
				_$("operate", fileid).innerHTML = '<a href="javascript:" data-type="pause" data-id="' + fileid + '">暂停</a>'
			},
			keep: function (fileid) {
				_$("status", fileid).innerHTML = '<span class="waiting">等待续传...</span>'
			},
			success: function (fileid, time) {
				var eleList = $("filelist_" + fileid),
				eleOperate = $("fileoperate_" + fileid),
				eleStatus = $("filestatus_" + fileid);
				this.backgroundSize(eleList, "0");
				eleStatus.id = "";
				eleOperate.id = "";
				eleList.id = "";
				localStorage.removeItem(fileid);
				eleStatus.innerHTML = '<span class="success">' + ((performance.now() - time > 1000) ? "上" : "秒") + "传成功！</span>";
				eleOperate.innerHTML = "";
				console.log([performance.now(), time].join())
			},
			error: function (fileid) {
				_$("status", fileid).innerHTML = '<span class="error">出现异常！</span>';
				_$("operate", fileid).innerHTML = '<a href="javascript:" data-type="try" data-id="' + fileid + '">重试</a>'
			}
		}
	})();
	var funFileUpload = function (fileid, onsuccess, onerror, onpause) {
		var file = fileArray[fileid],
		now = performance.now();
		if (!fileid || !file) {
			return
		}
		onsuccess = onsuccess || function () {
			funFileUpload(fileArray[0])
		};
		onerror = onerror || function () {
			funFileUpload(fileArray[fileArray.indexOf(fileid) + 1])
		};
		onpause = onpause || function () {
			funFileUpload(fileArray[fileArray.indexOf(fileid) + 1])
		};
		if (file.flagPause == true) {
			onpause.call(fileid);
			return
		}
		objStateElement.wait(fileid);
		var size = file.size,
		start = $("filelist_" + fileid).filesize;
		if (size == start) {
			fileArray.shift();
			if (delete fileArray[fileid]) {
				console.log(fileArray.join() + "---上传成功")
			}
			objStateElement.success(fileid, now);
			onsuccess.call(fileid, {});
			return
		}
		var funFileSize = function () {
			if (file.flagPause == true) {
				onpause.call(fileid);
				return
			}
			var data = new FormData(); // 表单数据对象，仅支持高版本浏览器
			data.append("name", encodeURIComponent(file.name));
			data.append("fileid", fileid);
			data.append("file", file.slice(start, start + fileSplitSize));
			data.append("start", start + "");
			var xhr = new XMLHttpRequest();
			xhr.open("post", eleForm.action, true);
			xhr.setRequestHeader("X_Requested_With", location.href.split("/")[5].replace(/[^a-z]+/g, "$"));
			xhr.upload.addEventListener("progress", function (e) {
				objStateElement.backgroundSize(fileid, (e.loaded + start) / size * 100)
			}, false);
			xhr.onreadystatechange = function (e) {
				if (xhr.readyState == 4) {
					if (xhr.status == 200) {
						try {
							var json = JSON.parse(xhr.responseText)
						} catch (e) {
							objStateElement.error(fileid);
							return
						}
						if (!json || !json.succ) {
							objStateElement.error(fileid);
							onerror.call(fileid, json);
							return
						}
						if (start + fileSplitSize >= size) {
							fileArray.shift();
							if (delete fileArray[fileid]) {
								console.log(fileArray.join() + "---上传成功")
							}
							objStateElement.success(fileid, now);
							onsuccess.call(fileid, json)
						} else {
							localStorage.setItem(fileid, start + "");
							start += fileSplitSize;
							console.log(start);
							funFileSize()
						}
					} else {
						objStateElement.error(fileid)
					}
				}
			};
			xhr.send(data)
		};
		funFileSize()
	};
	eleForm.addEventListener("submit", function (event) {
		funFileUpload(fileArray[0]);
		event.preventDefault()
	});
	eleFile.addEventListener("change", function (event) {
		var files = event.target.files;
		var htmlFile = "",
		index = 0,
		length = files.length;
		for (; index < length; index++) {
			var file = files[index];
			var name = file.name,
			size = file.size,
			type = file.type || "",
			id = (file.lastModifiedDate + "").replace(/\W/g, "") + size + type.replace(/\W/g, "");
			var objHtml = {
				id: id,
				type: "cloud",
				name: name,
				size: size + "B",
				status: '<span class="waiting">等待上传</span>',
				operate: '<a href="javascript:" data-type="delete" data-id="' + id + '">删除</a>'
			};
			if (name.length > 50) {
				objHtml.name = '<span title="' + name + '">' + name.slice(0, 20) + "..." + name.slice(-20) + "</span>"
			}
			var format = name.split(".");
			objHtml.type = format[format.length - 1] || "unknown";
			if (size > 1024 * 1024) {
				objHtml.size = Math.round(size / (1024 * 1024) * 10) / 10 + "M";
			} else {
				if (size > 1024) {
					objHtml.size = Math.round(size / 1024 * 10) / 10 + "KB";
				}
			}
			if (size > 1024 * 1024 * 50) { // 限制大小50M
				objHtml.id = Math.random();
				objHtml.status = '<span class="error">文件过大</span>';
				objHtml.operate = ""
			} else {
				if (fileArray.indexOf(id) != -1) {
					objHtml.id = Math.random();
					objHtml.status = '<span class="error">文件已存在</span>';
					objHtml.operate = ""
				} else {
					fileArray.push(id);
					fileArray[id] = file
				}
			}
			htmlFile += htmlTemplate.replace(/\$\w+\$/gi, function (matchs) {
				var returns = objHtml[matchs.replace(/\$/g, "")];
				return (returns + "") == "undefined" ? "" : returns
			})
		}
		if (htmlFile !== "") {
			eleSubmit.style.visibility = "visible";
			eleUploadUl.style.display = "table";
			eleUploadUl.insertAdjacentHTML("beforeEnd", htmlFile);
			var nameArray = fileArray.map(function (fileid) {
					var nameSplit = fileArray[fileid].name.split("."),
					name = nameSplit[nameSplit.length - 1];
					return fileid + "." + name
				});
			var xhr_filesize = new XMLHttpRequest();
			xhr_filesize.open("GET", "filesize.php?filename=" + nameArray.join(), true);
			xhr_filesize.onreadystatechange = function (e) {
				if (xhr_filesize.readyState == 4) {
					if (xhr_filesize.status == 200 && xhr_filesize.responseText) {
						var json = JSON.parse(xhr_filesize.responseText);
						if (json.succ && json.data) {
							for (var key in json.data) {
								if (json.data[key] > 0 && json.data[key] < fileArray[key].size) {
									objStateElement.backgroundSize(key, json.data[key] / fileArray[key].size * 100);
									objStateElement.keep(key)
								}
								$("filelist_" + key).filesize = json.data[key]
							}
						}
					}
				}
			};
			xhr_filesize.send()
		}
		eleForm.reset()
	});
	eleUploadUl.addEventListener("click", function (event) {
		var target = event.target,
		id = target && target.getAttribute("data-id");
		if (id && /^a$/i.test(target.tagName)) {
			switch (target.getAttribute("data-type")) {
			case "delete":
				var filelist = $("filelist_" + id);
				if (filelist) {
					filelist.style.opacity = 0;
					fileArray.splice(fileArray.indexOf(id), 1);
					if (delete fileArray[id]) {
						console.log(fileArray.join() + "---删除成功")
					}
					setTimeout(function () {
						filelist.parentNode.removeChild(filelist);
						if (fileArray.length == 0) {
							eleSubmit.style.visibility = "hidden";
							eleUploadUl.style.display = "none"
						}
					}, 220)
				}
				break;
			case "pause":
				var eleStatus = $("filestatus_" + id);
				if (fileArray[id]) {
					fileArray[id].flagPause = true;
					target.setAttribute("data-type", "reupload");
					target.innerHTML = "继续";
					if (eleStatus) {
						eleStatus.innerHTML = "上传暂停"
					}
				}
				break;
			case "try":
			case "reupload":
				funFileUpload(id, function () {}, function () {}, function () {})
			}
		}
	})
} else {
	eleUploadUl.style.display = "block";
	eleUploadUl.innerHTML = '<li class="error"><p style="margin:.5em 1em;">当前浏览器不支持！试试IE10+, Chrome等~</p></li>'
};
</script>
<div id="footer">
    Designed &amp; Powerd by <a href="/">csii</a><br />
    Copyright© CSII<br>
    <a href="http://www.miibeian.gov.cn/" target="_blank">**************</a>      
</div>

<div id="ad">
	<script type="text/javascript">	google_ad_client = "pub-0090627341039040";google_ad_slot = "2041257798";google_ad_width = 468;google_ad_height = 60;</script><script type="text/javascript" src="http://pagead2.googlesyndication.com/pagead/show_ads.js"></script>
    <script type="text/javascript">
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-11205167-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
		var ga = document.createElement('script');
		 ga.type = 'text/javascript';
		 ga.async = true;
		ga.src = 'http://www.google-analytics.com/ga.js';
		var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	</script>
</div>
</body>
</html>
