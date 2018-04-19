<%--
  #%L
  useragent-swagger-ui
  %%
  Copyright (C) 2012 - 2013 Temenos Holdings N.V.
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  #L%
  --%>
<%@page import="java.net.URL"%>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="java.nio.charset.StandardCharsets"%>
<%
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Headers","Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
    response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST");
    request.setAttribute("API_DOCS_URL", request.getContextPath() + "/api-docs");
    
    String defaultEntry = null;
    Set<String> resourcePaths = request.getServletContext().getResourcePaths("/api-docs/");
    for(String path : resourcePaths) {
        if(path!=null && path.length() > 0) {
            defaultEntry = path;
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Swagger UI</title>
<link rel="icon" type="image/png" href="images/favicon-32x32.png" sizes="32x32" />
<link rel="icon" type="image/png" href="images/favicon-16x16.png" sizes="16x16" />
<link href='css/typography.css' media='screen' rel='stylesheet' type='text/css' />
<link href='css/reset.css' media='screen' rel='stylesheet' type='text/css' />
<link href='css/screen.css' media='screen' rel='stylesheet' type='text/css' />
<link href='css/reset.css' media='print' rel='stylesheet' type='text/css' />
<link href='css/print.css' media='print' rel='stylesheet' type='text/css' />

<script src='lib/object-assign-pollyfill.js' type='text/javascript'></script>
<script src='lib/jquery-1.8.0.min.js' type='text/javascript'></script>
<script src='lib/jquery.slideto.min.js' type='text/javascript'></script>
<script src='lib/jquery.wiggle.min.js' type='text/javascript'></script>
<script src='lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
<script src='lib/handlebars-4.0.5.js' type='text/javascript'></script>
<script src='lib/lodash.min.js' type='text/javascript'></script>
<script src='lib/backbone-min.js' type='text/javascript'></script>
<script src='swagger-ui.js' type='text/javascript'></script>
<script src='lib/highlight.9.1.0.pack.js' type='text/javascript'></script>
<script src='lib/highlight.9.1.0.pack_extended.js' type='text/javascript'></script>
<script src='lib/jsoneditor.min.js' type='text/javascript'></script>
<script src='lib/marked.js' type='text/javascript'></script>
<script src='lib/swagger-oauth.js' type='text/javascript'></script>

<!-- Some basic translations -->
<!-- <script src='lang/translator.js' type='text/javascript'></script> -->
<!-- <script src='lang/ru.js' type='text/javascript'></script> -->
<!-- <script src='lang/en.js' type='text/javascript'></script> -->

<script type="text/javascript">
    $(function () {
	      var url = window.location.search.match(/url=([^&]+)/);
	      var pathArray = location.href.split( '/' );
	      var protocol = pathArray[0];
	      var host = pathArray[2];
	      var project = pathArray[3];
	      var base = protocol + '//' + host + '/' + project;
	      if (url && url.length > 1) {
	              url = url[1];
	      } else {
	      	
	          <%
	            if (defaultEntry != null) {
	                %> url = base + "/api-docs?file=" + "<%= defaultEntry %>"<%
	            } else {
	                %> url = "http://petstore.swagger.io/v2/swagger.json"<%
	            }
	          
	          %>
	      }

        hljs.configure({
            highlightSizeThreshold : 5000
        });

        // Pre load translate...
        if (window.SwaggerTranslator) {
            window.SwaggerTranslator.translate();
        }
        window.swaggerUi = new SwaggerUi(
                {
                    url : url,
                    dom_id : "swagger-ui-container",
                    supportedSubmitMethods : [ 'get', 'post', 'put', 'delete',
                            'patch' ],
                    onComplete : function(swaggerApi, swaggerUi) {
                        if (typeof initOAuth == "function") {
                            initOAuth({
                                clientId : "your-client-id",
                                clientSecret : "your-client-secret-if-required",
                                realm : "your-realms",
                                appName : "your-app-name",
                                scopeSeparator : " ",
                                additionalQueryStringParams : {}
                            });
                        }
                        
                        $('pre code').each(function(i, e) {
                            hljs.highlightBlock(e)
                        });

                        if (window.SwaggerTranslator) {
                            window.SwaggerTranslator.translate();
                        }
                    },
                    onFailure : function(data) {
                        log("Unable to Load SwaggerUI");
                    },
                    docExpansion : "none",
                    jsonEditor : false,
                    defaultModelRendering : 'schema',
                    showRequestHeaders : false
                });
        
        $('#input_apiKey').change(function() {
            var key = $('#input_apiKey')[0].value;
            console.log("key: " + key);
            if(key && key.trim() != "") {
                console.log("added key " + key);
                swaggerUi.api.clientAuthorizations.add("key", new SwaggerClient.ApiKeyAuthorization("Authorization", key, "header"));
            }
        })

        window.swaggerUi.load();

        function log() {
            if ('console' in window) {
                console.log.apply(console, arguments);
            }
        }
    });
</script>
</head>

<body class="swagger-section">
	<div id='header'>
		<div class="swagger-ui-wrap">
			<a id="logo" href="http://swagger.io"><img class="logo__img" alt="swagger" height="30" width="30"
				src="images/logo_small.png" /><span class="logo__title">swagger</span></a>
			<form id='api_selector'>
			<%
            String dateTimeString = Long.toString(new Date().getTime());
            byte[] nonceByte = dateTimeString.getBytes(StandardCharsets.UTF_8);
            String nonce= Base64.getEncoder().encodeToString(nonceByte);
            %>
             <input name="nonce" type="hidden" value="<%=nonce %>"></input>
				<div class="input">
					<select id="input_baseUrl" name="baseUrl">
					</select>
				</div>
				<div id='auth_container'></div>
				<div class='input'>
					<a id="explore" class="header__btn" href="#" data-sw-translate>Explore</a>
				</div>
			</form>
		</div>
	</div>

	<div id="message-bar" class="swagger-ui-wrap" data-sw-translate>&nbsp;</div>
	<div id="swagger-ui-container" class="swagger-ui-wrap"></div>
	<script>
    
        var url = window.location.search.match(/url=([^&]+)/);
        var pathArray = location.href.split('/');
        var protocol = pathArray[0];
        var host = pathArray[2];
        var project = pathArray[3];
        var base = protocol + '//' + host + '/' + project;
        
        var spec = {
                <%
                for(String json : resourcePaths) {
                    %>'<%= json.replace(".json", "").replace("api-docs-", "").replace("/api-docs/", "") %>' : base + "/" + "api-docs?file="+"<%= json %>",<%
                }
                %>
                }

        var sel = $('#input_baseUrl');

        $.each(spec, function(key, val) {
            var opt = $('<option>').prop('value', val).html(key);
            sel.append(opt);
        });
    </script>
</body>
</html>
