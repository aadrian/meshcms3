/** 
 * flashembed 0.31. Adobe Flash embedding script
 * 
 * http://flowplayer.org/tools/flash-embed.html
 *
 * Copyright (c) 2008 Tero Piirainen (tipiirai@gmail.com)
 *
 * Released under the MIT License:
 * http://www.opensource.org/licenses/mit-license.php
 * 
 * >> Basically you can do anything you want but leave this header as is <<
 *
 * version 0.01 - 03/11/2008 
 * version 0.31 - Tue Jul 22 2008 06:30:32 GMT+0200 (GMT+02:00)
 */
eval(function(p,a,c,k,e,r){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)r[e(c)]=k[c]||e(c);k=[function(e){return r[e]}];e=function(){return'\\w+'};c=1};while(c--)if(k[c])p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c]);return p}('8 q(f,e,m){8 12(){4 b="";3(u m==\'8\'){m=m()}3(E.Q&&E.1k&&E.1k.1h){b=\'<1M 1F="1C/x-1s-18" \';3(l.w){G(l,{R:l.w})}F(4 a B l){3(l[a]!==z){b+=[a]+\'="\'+l[a]+\'"\\n\\t\'}}3(m){b+=\'1m=\\\'\'+1d(m)+\'\\\'\'}b+=\'/>\'}y{b=\'<D 2A="2x:2s-2q-2n-2k-2i" \';b+=\'U="\'+l.U+\'" N="\'+l.N+\'"\';3(!l.w&&M.28){l.w="26"+(""+25.23()).1n(5)}3(l.w){b+=\' w="\'+l.w+\'"\'}b+=\'>\';b+=\'\\n\\t<11 R="1W" Y="\'+l.H+\'" />\';l.w=l.H=l.U=l.N=z;F(4 k B l){3(l[k]!==z){b+=\'\\n\\t<11 R="\'+k+\'" Y="\'+l[k]+\'" />\'}}3(m){b+=\'\\n\\t<11 R="1m" Y=\\\'\'+1d(m)+\'\\\' />\'}b+="</D>";3(p){1Q(b)}}9 b}8 1f(d){4 c=1I(8(){4 a=M;4 b=a.Z(d);3(b){q(b,e,m);1E(c)}y 3(a&&a.2H&&a.Z&&a.2D){1E(c)}},13);9 1B}8 G(b,a){3(a){F(O B a){3(a.16(O)){b[O]=a[O]}}}}4 l={H:\'#\',U:\'1x%\',N:\'1x%\',A:z,V:z,1b:z,1a:1t,2o:\'#2m\',2l:1B,2j:\'1q\',2h:\'2g\',1F:\'1C/x-1s-18\',1p:\'2d://2c.2b.2a/29/27\'};3(u e==\'15\'){e={H:e}}G(l,e);4 h=q.K();4 g=l.A;4 o=l.1b;4 p=l.1a;3(u f==\'15\'){4 n=M.Z(f);3(n){f=n}y{9 1f(f)}}3(!f){9}3(!g||q.14(g)){l.V=l.A=l.1b=l.1a=z;f.I=12();9 f.21}y 3(l.V){4 j=l.V.1Z(l,q.K(),m);3(j){f.I=j}}y 3(g&&o&&q.14([6,1Y])){G(l,{H:o});m={1X:1V.1j,1U:\'1T\',1S:M.1R};f.I=12()}y{3(f.I.v(/\\s/g,\'\')!==\'\'){}y{f.I="<1i>X A "+g+" 1P 1O 1g 1N</1i>"+"<1l>"+(h[0]>0?"1L A 1g "+h:"1K 1J 20 18 1H 22")+"</1l>"+"<p>1G 24 A 2J <a 1j=\'"+l.1p+"\'>2I</a></p>"}}8 1d(a){4 b="";F(4 c B a){3(a[c]){b+=[c]+\'=\'+W(a[c])+\'&\'}}9 b.1n(0,b.1h-1)}8 W(b){2C(1D(b)){T\'15\':9\'"\'+b.v(L 2B(\'(["\\\\\\\\])\',\'g\'),\'\\\\$1\')+\'"\';T\'1A\':9\'[\'+1z(b,8(a){9 W(a)}).1o(\',\')+\']\';T\'8\':9\'"8()"\';T\'D\':4 c=[];F(4 d B b){3(b.16(d)){c.1y(\'"\'+d+\'":\'+W(b[d]))}}9\'{\'+c.1o(\',\')+\'}\'}9 2y(b).v(/\\s/g," ").v(/\\\'/g,"\\"")}8 1D(a){3(a===z||a===19){9 1t}4 b=u a;9(b==\'D\'&&a.1y)?\'1A\':b}3(17.1w){17.1w("2w",8(){2v=8(){};2t=8(){}})}8 1z(c,a){4 b=[];F(4 i B c){3(c.16(i)){b[i]=a(c[i])}}9 b}9 f}3(u 1v==\'8\'){(8($){$.2r.G({q:8(b,a){9 1u.2p(8(){q(1u,b,a)})}})})(1v)}q=q||{};q.K=8(){4 d=[0,0];3(E.Q&&u E.Q["1r X"]=="D"){4 f=E.Q["1r X"].2u;3(u f!="19"){f=f.v(/^.*\\s+(\\S+\\s+\\S+$)/,"$1");4 c=J(f.v(/^(.*)\\..*$/,"$1"),10);4 a=/r/.2z(f)?J(f.v(/^.*r(.*)$/,"$1"),10):0;d=[c,a]}}y 3(17.P){1e{4 b=L P("C.C.7")}1c(e){1e{b=L P("C.C.6");d=[6,0];b.2E="1q"}1c(2F){3(d[0]==6){9}}1e{b=L P("C.C")}1c(2G){}}3(u b=="D"){f=b.2f("$A");3(u f!="19"){f=f.v(/^\\S+\\s+(.*)$/,"$1").2e(",");d=[J(f[0],10),J(f[2],10)]}}}9 d};q.14=8(c){4 a=q.K();4 b=(a[0]>c[0])||(a[0]==c[0]&&a[1]>=c[1]);9 b};',62,170,'|||if|var||||function|return|||||||||||||||||flashembed||||typeof|replace|id||else|null|version|in|ShockwaveFlash|object|navigator|for|extend|src|innerHTML|parseInt|getVersion|new|document|height|key|ActiveXObject|plugins|name||case|width|onFail|asString|Flash|value|getElementById||param|getHTML||isSupported|string|hasOwnProperty|window|flash|undefined|debug|expressInstall|catch|concatVars|try|init|is|length|h2|href|mimeTypes|h3|flashvars|substring|join|pluginspage|always|Shockwave|shockwave|false|this|jQuery|attachEvent|100|push|map|array|true|application|typeOf|clearInterval|type|Download|plugin|setInterval|have|You|Your|embed|required|greater|or|alert|title|MMdoctitle|PlugIn|MMplayerType|location|movie|MMredirectURL|65|call|no|firstChild|installed|random|latest|Math|_|getflashplayer|all|go|com|adobe|www|http|split|GetVariable|high|quality|444553540000|allowscriptaccess|96B8|allowfullscreen|ffffff|11cf|bgcolor|each|AE6D|fn|D27CDB6E|__flash_savedUnloadHandler|description|__flash_unloadHandler|onbeforeunload|clsid|String|test|classid|RegExp|switch|body|AllowScriptAccess|ee|eee|getElementsByTagName|here|from'.split('|'),0,{}))