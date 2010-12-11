<%@ page isELIgnored="false" session="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    ${mesh.head}

    <meta http-equiv="Content-Type" content="text/html; charset=${mesh.encoding}" />
    <title>${mesh.title}</title>

    <c:set target="${mesh}" property="file" value="${mesh.themePath}/style.css"/>
    <c:set target="${mesh}" property="file" value="${mesh.meshPath}/lib/colorbox/colorbox.css"/>
    <c:set target="${mesh}" property="file" value="${mesh.meshPath}/meshcms.css"/>
    <c:if test="${mesh.site.userCSS ne null}">
      <c:set target="${mesh}" property="file" value="${mesh.site.userCSS}"/>
    </c:if>
    <link rel="stylesheet" href="${mesh.concatFiles}" type="text/css" media="screen" />
    <!--[if IE 6]><link rel="stylesheet" href="${mesh.themePath}/style.ie6.css" type="text/css" media="screen" /><![endif]-->
    <!--[if IE 7]><link rel="stylesheet" href="${mesh.themePath}/style.ie7.css" type="text/css" media="screen" /><![endif]-->

    <c:set target="${mesh}" property="file" value="${mesh.themePath}/script.js"/>
    <c:set target="${mesh}" property="file" value="${mesh.meshPath}/lib/jquery-1.4.2.min.js"/>
    <c:set target="${mesh}" property="file" value="${mesh.meshPath}/lib/colorbox/jquery.colorbox-1.3.6-min.js"/>
    <script type="text/javascript" src="${mesh.concatFiles}"></script>

    <c:if test="${not empty mesh.description}">
      <meta name="description" content="${mesh.description}" />
    </c:if>
    <c:if test="${not empty mesh.keywords}">
      <meta name="keywords" content="${mesh.keywords}" />
    </c:if>

    <c:set target="${mesh}" property="zone" value="canonical"/>
    <c:set target="${mesh}" property="module" value="canonicalurl"/>
    <%-- <c:set target="${mesh.parameters}" property="root" value="http://www.yoursite.com"/> --%>
    ${mesh.zone}

    <c:set target="${mesh}" property="zone" value="head"/>
    ${mesh.zone}
  </head>
  <body>
    <div id="art-page-background-gradient"></div>
    <div id="art-main">
      <div class="art-sheet">
        <div class="art-sheet-tl"></div>
        <div class="art-sheet-tr"></div>
        <div class="art-sheet-bl"></div>
        <div class="art-sheet-br"></div>
        <div class="art-sheet-tc"></div>
        <div class="art-sheet-bc"></div>
        <div class="art-sheet-cl"></div>
        <div class="art-sheet-cr"></div>
        <div class="art-sheet-cc"></div>
        <div class="art-sheet-body">
          <div class="art-header">
            <div class="art-header-png"></div>
            <div class="art-header-jpeg"></div>
            <div class="art-logo">
              <h1 id="name-text" class="art-logo-name"><a href="/">${mesh.site.name}</a></h1>
              <div id="slogan-text" class="art-logo-text">${mesh.site.slogan}</div>
            </div>
          </div>
          <div class="art-nav">
            <div class="l"></div>
            <div class="r"></div>
            <c:set target="${mesh}" property="zone" value="top_menu"/>
            <c:set target="${mesh}" property="module" value="langmenu"/>
            <c:set target="${mesh.parameters}" property="style" value="art-menu"/>
            ${mesh.zone}
          </div>
          <div class="art-content-layout">
            <div class="art-content-layout-row">
              <div class="art-layout-cell art-content">
                <c:set target="${mesh}" property="zone" value="body"/>
                <c:forEach items="${mesh.modules}" var="m">
                  <div class="art-post">
                    <div class="art-post-tl"></div>
                    <div class="art-post-tr"></div>
                    <div class="art-post-bl"></div>
                    <div class="art-post-br"></div>
                    <div class="art-post-tc"></div>
                    <div class="art-post-bc"></div>
                    <div class="art-post-cl"></div>
                    <div class="art-post-cr"></div>
                    <div class="art-post-cc"></div>
                    <div class="art-post-body">
                      <div class="art-post-inner art-article">
                        <c:if test="${not empty m.title}">
                          <h2 class="art-postheader">
                            ${m.title}
                          </h2>
                        </c:if>
                        <c:if test="${not empty m.content}">
                          <div class="art-postcontent">
                            ${m.content}
                          </div>
                        </c:if>
                        <div class="cleared"></div>
                        <c:if test="${not empty m.notes}">
                          <div class="art-postmetadatafooter">
                            <div class="art-postfootericons art-metadata-icons">
                              ${m.notes}
                            </div>
                          </div>
                        </c:if>
                      </div>

                      <div class="cleared"></div>
                    </div>
                  </div>
                </c:forEach>
              </div>
              <div class="art-layout-cell art-sidebar1">
                <div class="art-vmenublock">
                  <div class="art-vmenublock-body">
                    <div class="art-vmenublockcontent">
                      <div class="art-vmenublockcontent-body">
                        <c:set target="${mesh}" property="zone" value="section_menu"/>
                        <c:set target="${mesh}" property="module" value="menu"/>
                        <c:set target="${mesh.parameters}" property="rootType" value="langRoot"/>
                        <c:set target="${mesh.parameters}" property="onPathItems" value="true"/>
                        <c:set target="${mesh.parameters}" property="firstLevelItems" value="true"/>
                        <c:set target="${mesh.parameters}" property="sameLevelItems" value="true"/>
                        <c:set target="${mesh.parameters}" property="intermediateLevelItems" value="true"/>
                        <c:set target="${mesh.parameters}" property="childItems" value="true"/>
                        <c:set target="${mesh.parameters}" property="linkCurrent" value="true"/>
                        <c:set target="${mesh.parameters}" property="style" value="art-vmenu"/>
                        <c:set target="${mesh.parameters}" property="rootLabel" value="Home"/>
                        ${mesh.zone}
                        <div class="cleared"></div>
                      </div>
                    </div>
                    <div class="cleared"></div>
                  </div>
                </div>
                <div class="art-block">
                  <div class="art-block-tl"></div>
                  <div class="art-block-tr"></div>
                  <div class="art-block-bl"></div>
                  <div class="art-block-br"></div>
                  <div class="art-block-tc"></div>
                  <div class="art-block-bc"></div>
                  <div class="art-block-cl"></div>
                  <div class="art-block-cr"></div>
                  <div class="art-block-cc"></div>
                  <div class="art-block-body">
                    <div class="art-blockheader">
                      <div class="l"></div>
                      <div class="r"></div>
                      <div class="t">
                        <c:choose>
                          <c:when test="${mesh.lang == 'it'}">
                            Cerca
                          </c:when>
                          <c:otherwise>
                            Search
                          </c:otherwise>
                        </c:choose>
                      </div>
                    </div>
                    <div class="art-blockcontent">
                      <div class="art-blockcontent-body">
                        <form method="get" name="searchform" action="http://www.google.com/search">
                          <input type="hidden" name="as_sitesearch" value="${mesh.site.host}" />
                          <input type="text" value="" name="as_q" style="width: 95%;" /><span class="art-button-wrapper">
                            <span class="l"> </span>
                            <span class="r"> </span>
                            <c:choose>
                              <c:when test="${mesh.lang == 'it'}">
                                <input class="art-button" type="submit" name="search" value="Cerca" />
                              </c:when>
                              <c:otherwise>
                                <input class="art-button" type="submit" name="search" value="Search" />
                              </c:otherwise>
                            </c:choose>
                          </span>
                        </form>
                        <div class="cleared"></div>
                      </div>
                    </div>
                    <div class="cleared"></div>
                  </div>
                </div>
                <c:set target="${mesh}" property="zone" value="aside"/>
                <c:set target="${mesh}" property="module" value="login"/>
                <c:set target="${mesh}" property="action" value="add_before"/>
                <c:forEach items="${mesh.modules}" var="m">
                  <div class="art-block">
                    <div class="art-block-tl"></div>
                    <div class="art-block-tr"></div>
                    <div class="art-block-bl"></div>
                    <div class="art-block-br"></div>
                    <div class="art-block-tc"></div>
                    <div class="art-block-bc"></div>
                    <div class="art-block-cl"></div>
                    <div class="art-block-cr"></div>
                    <div class="art-block-cc"></div>
                    <div class="art-block-body">
                      <c:if test="${not empty m.title}">
                        <div class="art-blockheader">
                          <div class="l"></div>
                          <div class="r"></div>
                          <div class="t">${m.title}</div>
                        </div>
                      </c:if>
                      <c:if test="${not empty m.content}">
                        <div class="art-blockcontent">
                          <div class="art-blockcontent-body">
                            ${m.content}
                            <div class="cleared"></div>
                          </div>
                        </div>
                      </c:if>
                      <div class="cleared"></div>
                    </div>
                  </div>
                </c:forEach>
              </div>
            </div>
          </div>
          <div class="cleared"></div><div class="art-footer">
            <div class="art-footer-t"></div>
            <div class="art-footer-l"></div>
            <div class="art-footer-b"></div>
            <div class="art-footer-r"></div>
            <div class="art-footer-body">
              <div class="art-footer-text">
                <p>Copyright &copy; <a href="${mesh.site.ownerURL}">${mesh.site.owner}</a></p>
              </div>
              <div class="cleared"></div>
            </div>
          </div>
          <div class="cleared"></div>
        </div>
      </div>
      <div class="cleared"></div>
      <p class="art-page-footer">Powered by <a href="http://www.cromoteca.com/meshcms/">${mesh.cms}</a>.</p>
    </div>

  </body>
</html>
