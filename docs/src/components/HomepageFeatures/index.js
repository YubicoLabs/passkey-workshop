import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

import ReactPlayer from "react-player";

const FeatureList = [
  {
    title: 'Authentication made easy',
    video: "/videos/auth-touchid.mp4",
    description: (
      <>
        Observe how seamless and intuitive it is to authenticate using a passkey. Your users can now increase their account security AND forgo the need to remember their login information
      </>
    ),
  },
  {
    title: 'Remove the anxiety of creating and managing passwords',
    video: "/videos/reg-touchid.mp4",
    description: (
      <>
        Observe how easy it is to add a new credential to your account. No complex password 
        requirements, no memorization of credentials, and no need to write your credentials down 
      </>
    ),
  },
];

function Feature({video, title, description}) {
  return (
    <div className={clsx('col col--12')}>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
      <div>
      <ReactPlayer
        controls
        width="100%"
        height="100%"
        url={video}
        style={{ marginBottom: "2em" }}
      />
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
