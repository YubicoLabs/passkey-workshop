"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[327],{3905:(e,t,a)=>{a.d(t,{Zo:()=>p,kt:()=>y});var r=a(7294);function i(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function n(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,r)}return a}function o(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?n(Object(a),!0).forEach((function(t){i(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):n(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function s(e,t){if(null==e)return{};var a,r,i=function(e,t){if(null==e)return{};var a,r,i={},n=Object.keys(e);for(r=0;r<n.length;r++)a=n[r],t.indexOf(a)>=0||(i[a]=e[a]);return i}(e,t);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);for(r=0;r<n.length;r++)a=n[r],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(i[a]=e[a])}return i}var c=r.createContext({}),l=function(e){var t=r.useContext(c),a=t;return e&&(a="function"==typeof e?e(t):o(o({},t),e)),a},p=function(e){var t=l(e.components);return r.createElement(c.Provider,{value:t},e.children)},u="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},h=r.forwardRef((function(e,t){var a=e.components,i=e.mdxType,n=e.originalType,c=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),u=l(a),h=i,y=u["".concat(c,".").concat(h)]||u[h]||d[h]||n;return a?r.createElement(y,o(o({ref:t},p),{},{components:a})):r.createElement(y,o({ref:t},p))}));function y(e,t){var a=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var n=a.length,o=new Array(n);o[0]=h;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s[u]="string"==typeof e?e:i,o[1]=s;for(var l=2;l<n;l++)o[l]=a[l];return r.createElement.apply(null,o)}return r.createElement.apply(null,a)}h.displayName="MDXCreateElement"},6646:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>c,contentTitle:()=>o,default:()=>d,frontMatter:()=>n,metadata:()=>s,toc:()=>l});var r=a(7462),i=(a(7294),a(3905));const n={sidebar_position:1},o="Architecture at a glance",s={unversionedId:"architecture/architecture-at-a-glance",id:"architecture/architecture-at-a-glance",title:"Architecture at a glance",description:"We will start by observing a high level architecture diagram of a standard passkey enabled application. This first diagram outlines the required components of our application. Please note that this diagram does NOT include the core functionality of your application, only the components necessary for passkey and authentication/authorization related actions",source:"@site/docs/architecture/architecture-at-a-glance.md",sourceDirName:"architecture",slug:"/architecture/architecture-at-a-glance",permalink:"/passkey-workshop/docs/architecture/architecture-at-a-glance",draft:!1,editUrl:"https://github.com/YubicoLabs/passkey-workshop/tree/main/docs/docs/architecture/architecture-at-a-glance.md",tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1},sidebar:"tutorialSidebar",previous:{title:"Architecture",permalink:"/passkey-workshop/docs/category/architecture"},next:{title:"Client application",permalink:"/passkey-workshop/docs/architecture/client-app"}},c={},l=[{value:"Components",id:"components",level:2},{value:"Authenticator",id:"authenticator",level:3},{value:"Client application",id:"client-application",level:3},{value:"Relying party",id:"relying-party",level:3},{value:"Application layer",id:"application-layer",level:4},{value:"Identity provider",id:"identity-provider",level:4},{value:"Credential repository",id:"credential-repository",level:4},{value:"Metadata repository",id:"metadata-repository",level:4}],p={toc:l},u="wrapper";function d(e){let{components:t,...n}=e;return(0,i.kt)(u,(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"architecture-at-a-glance"},"Architecture at a glance"),(0,i.kt)("p",null,"We will start by observing a high level architecture diagram of a standard passkey enabled application. This first diagram outlines the required components of our application. Please note that this diagram does ",(0,i.kt)("strong",{parentName:"p"},"NOT")," include the core functionality of your application, only the components necessary for passkey and authentication/authorization related actions"),(0,i.kt)("p",null,(0,i.kt)("img",{alt:"Passkey architecture diagram",src:a(8386).Z,width:"1095",height:"550"})),(0,i.kt)("h2",{id:"components"},"Components"),(0,i.kt)("p",null,"Below is a summary of the primary components of a passkey application."),(0,i.kt)("h3",{id:"authenticator"},"Authenticator"),(0,i.kt)("p",null,"Device or software that will be used as a token to prove the user\u2019s identity. WebAuthn relies on public key encryption - the authenticator will issue public keys to applications during registration, and will then use the corresponding private key to sign challenges issued during authentication."),(0,i.kt)("p",null,"Authenticators can come in the form of hardware and software. Examples of hardware based authenticators could include security keys where the private key is bound to the device, and cannot be extracted. Software based authenticators can be a combination of a device\u2019s TPM module and an application that allows the user to store their credentials in the cloud, to be used across their devices."),(0,i.kt)("h3",{id:"client-application"},"Client application"),(0,i.kt)("p",null,"The client application is the front end application utilized by your users. The primary use in passkey applications will be for users to complete their authentication and registration ceremonies."),(0,i.kt)("p",null,"The experience offered by the client will have some variety depending on the user\u2019s use of the operating system and browser, but should ultimately still provide the same degree of security and usability, assuming the ecosystem supports passkeys."),(0,i.kt)("h3",{id:"relying-party"},"Relying party"),(0,i.kt)("p",null,"The relying party is the backend application that will facilitate the authentication and registration ceremonies in order to determine if the user should be allowed to access their requested resources. The relying party can be broken down into smaller sub-components that all play a part in managing users."),(0,i.kt)("h4",{id:"application-layer"},"Application layer"),(0,i.kt)("p",null,"The application layer will be responsible for the core business logic required to complete registration, authentication, and user/credential management operations. This component will primarily be invoked by API\u2019s for specific actions. The primary goal will be to issue registration and authentication ceremonies, using inputs from the identity provider, and by referencing credentials stored in the credential repository."),(0,i.kt)("h4",{id:"identity-provider"},"Identity provider"),(0,i.kt)("p",null,"This is the service that will allow your application to manage users, and issue authorization tokens (such as OAuth2) to access its resources. This can be an in-house built solution, or a solution purchased from a technology vendor."),(0,i.kt)("p",null,"Regardless of the type of solution, it will either need to support passkeys (WebAuthn) or allow you to create a custom flow that can interact with your application layer to facilitate the registration, and authentication ceremonies."),(0,i.kt)("h4",{id:"credential-repository"},"Credential repository"),(0,i.kt)("p",null,"This is the repository of user credentials (public keys) that were sent to the relying party during registration, and leveraged during the authentication ceremony. An advantage of using passkeys rather than passwords is the severity of this repository being compromised is mitigated. This repository can operate by only storing the public key, credential IDs, and associated user handle. If this repository is leaked, an attacker cannot leverage the public keys, without access to the authenticator with the private key."),(0,i.kt)("h4",{id:"metadata-repository"},"Metadata repository"),(0,i.kt)("p",null,"This is an optional component of your relying party, but should be included as a best practice. A metadata repository will allow your application to identify the make and model of a registered authenticator, if permission was granted by the user during registration."),(0,i.kt)("p",null,"On the surface level this data can help the user experience by helping users and administrators understand more details about their authenticator. Digging deeper, this repository can help you impose different degrees of authenticator management, in high assurance scenarios that require some control over what can and cannot register in your environment. More will be covered on this when we discuss attestation."),(0,i.kt)("p",null,"The next few sections will provide more details around our implementation of these components, best practices, and general considerations."))}d.isMDXComponent=!0},8386:(e,t,a)=>{a.d(t,{Z:()=>r});const r=a.p+"assets/images/passkey_arch-18c3a64c88d8e205bb926c7b485385ae.jpg"}}]);