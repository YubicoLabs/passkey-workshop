"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[707],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>f});var a=n(7294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function r(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?r(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):r(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,a,i=function(e,t){if(null==e)return{};var n,a,i={},r=Object.keys(e);for(a=0;a<r.length;a++)n=r[a],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(a=0;a<r.length;a++)n=r[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var l=a.createContext({}),p=function(e){var t=a.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},c=function(e){var t=p(e.components);return a.createElement(l.Provider,{value:t},e.children)},d="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},h=a.forwardRef((function(e,t){var n=e.components,i=e.mdxType,r=e.originalType,l=e.parentName,c=s(e,["components","mdxType","originalType","parentName"]),d=p(n),h=i,f=d["".concat(l,".").concat(h)]||d[h]||u[h]||r;return n?a.createElement(f,o(o({ref:t},c),{},{components:n})):a.createElement(f,o({ref:t},c))}));function f(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var r=n.length,o=new Array(r);o[0]=h;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[d]="string"==typeof e?e:i,o[1]=s;for(var p=2;p<r;p++)o[p]=n[p];return a.createElement.apply(null,o)}return a.createElement.apply(null,n)}h.displayName="MDXCreateElement"},6100:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>u,frontMatter:()=>r,metadata:()=>s,toc:()=>p});var a=n(7462),i=(n(7294),n(3905));const r={sidebar_position:1},o="API definition",s={unversionedId:"relying-party/api-def",id:"relying-party/api-def",title:"API definition",description:"This section will discuss the API that defines a client's ability to interact with the relying party (RP) application. By the end of this section you should understand best practices for the API that should translate to better comprehension on how the application should behave.",source:"@site/docs/relying-party/api-def.md",sourceDirName:"relying-party",slug:"/relying-party/api-def",permalink:"/docs/relying-party/api-def",draft:!1,editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/relying-party/api-def.md",tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1},sidebar:"tutorialSidebar",previous:{title:"Relying Party",permalink:"/docs/category/relying-party"},next:{title:"Data sources and RP configurations",permalink:"/docs/relying-party/config-and-data"}},l={},p=[{value:"Prerequisites",id:"prerequisites",level:2},{value:"Accessing the API documentation",id:"accessing-the-api-documentation",level:2},{value:"Overview of API methods",id:"overview-of-api-methods",level:2},{value:"API status",id:"api-status",level:3},{value:"Registration",id:"registration",level:3},{value:"Authentication",id:"authentication",level:3},{value:"Credential management",id:"credential-management",level:3}],c={toc:p},d="wrapper";function u(e){let{components:t,...n}=e;return(0,i.kt)(d,(0,a.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"api-definition"},"API definition"),(0,i.kt)("p",null,"This section will discuss the API that defines a client's ability to interact with the relying party (",(0,i.kt)("strong",{parentName:"p"},"RP"),") application. By the end of this section you should understand best practices for the API that should translate to better comprehension on how the application should behave."),(0,i.kt)("p",null,"Note that these best practices are for general passkey applications. The API defined in this section will evolve in later sections to accommodate different use cases."),(0,i.kt)("h2",{id:"prerequisites"},"Prerequisites"),(0,i.kt)("p",null,"Ensure that you have deployed the RP sample found in this project."),(0,i.kt)("p",null,(0,i.kt)("a",{parentName:"p",href:"/docs/deploy"},"Follow the instructions on this page to deploy the application.")),(0,i.kt)("h2",{id:"accessing-the-api-documentation"},"Accessing the API documentation"),(0,i.kt)("p",null,"Once your project is deployed, the API documentation can be found at: ",(0,i.kt)("a",{parentName:"p",href:"http://localhost:8080"},"http://localhost:8080")),(0,i.kt)("h2",{id:"overview-of-api-methods"},"Overview of API methods"),(0,i.kt)("p",null,"The section below will outline the responsibilities of the different API methods."),(0,i.kt)("h3",{id:"api-status"},"API status"),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"/status")," method can be used to check the availability of your application (if the service is running). This is not essential to passkey applications, but overall a best practice"),(0,i.kt)("h3",{id:"registration"},"Registration"),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"/attestation")," methods will be used to register a new passkey. Methods for the WebAuthn registration flow requires two calls."),(0,i.kt)("p",null,"The first call (",(0,i.kt)("a",{parentName:"p",href:"http://localhost:8080/swagger-ui/index.html#/v1/serverPublicKeyCredentialCreationOptionsRequest"},(0,i.kt)("inlineCode",{parentName:"a"},"/attestation/options")),") is used to receive an object that includes the options/configurations that should be used when creating a new credential."),(0,i.kt)("p",null,"The second call (",(0,i.kt)("a",{parentName:"p",href:"http://localhost:8080/swagger-ui/index.html#/v1/serverAuthenticatorAttestationResponse"},(0,i.kt)("inlineCode",{parentName:"a"},"/attestation/result")),") is used to send the newly created passkey to be stored in the RP."),(0,i.kt)("p",null,"This topic will be covered in more detail in the following section on ",(0,i.kt)("a",{parentName:"p",href:"/docs/relying-party/reg-flow"},"Registration Flows"),"."),(0,i.kt)("h3",{id:"authentication"},"Authentication"),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"/assertion")," methods will be used to authenticate using the authenticator that generated your passkey. Methods for the WebAuthn authentication flow requires two calls."),(0,i.kt)("p",null,"The first call (",(0,i.kt)("a",{parentName:"p",href:"http://localhost:8080/swagger-ui/index.html#/v1/serverPublicKeyCredentialGetOptionsRequest"},(0,i.kt)("inlineCode",{parentName:"a"},"/assertion/options")),") is used to receive an object that includes the options/configurations that should be used when trying to validate a challenge for authentication."),(0,i.kt)("p",null,"The second call (",(0,i.kt)("a",{parentName:"p",href:"http://localhost:8080/swagger-ui/index.html#/v1/serverAuthenticatorAssertionResponse"}," ",(0,i.kt)("inlineCode",{parentName:"a"},"/assertion/result")),") is used to send the signed challenge to verify if a valid credential was used."),(0,i.kt)("p",null,"This topic will be covered in more detail in the following section on ",(0,i.kt)("a",{parentName:"p",href:"/docs/relying-party/auth-flow"},"Authentication Flows"),"."),(0,i.kt)("h3",{id:"credential-management"},"Credential management"),(0,i.kt)("p",null,"The ",(0,i.kt)("inlineCode",{parentName:"p"},"/user/credentials")," methods will provide credential management. In passkey applications, credential management refers to the process of managing a user's credential by allowing them to delete or rename a credential."),(0,i.kt)("p",null,"In the current paradigm, ",(0,i.kt)("strong",{parentName:"p"},"password")," management best practices dictate that a user should rotate, or change their password frequently. This rotation is not needed for passkeys - in fact, the user should never be allowed to change the credential that was passed to the relying party."),(0,i.kt)("p",null,"The user should only have two options:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"Update attributes that are used to help them identify a passkey (like a nickname)"),(0,i.kt)("li",{parentName:"ul"},"Delete the passkey from the RP (good in the case of a lost or stolen authenticator)")),(0,i.kt)("p",null,"Both of these options are provided in our example. They can be tested once a credential has been registered to the RP."))}u.isMDXComponent=!0}}]);