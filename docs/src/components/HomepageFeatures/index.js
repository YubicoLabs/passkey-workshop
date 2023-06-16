import React from 'react';
import clsx from 'clsx';
import styles from './styles.module.css';

import ReactPlayer from "react-player";

const FeatureList = [
  {
    title: 'Authentication made easy',
    video: "https://www.youtube-nocookie.com/embed/9bv2Q4fQfp4",
    description: (
      <>
        Observe how seamless and intuitive it is to authenticate using a passkey. Your users can now increase their account security AND forgo the need to remember their login information
      </>
    ),
  },
];

function Feature() {
  return (
    <div className={clsx('col col--12')}>
      <div>
      <div className='player-wrapper'>
        <ReactPlayer
          controls
          className='react-player'
          url="https://www.youtube-nocookie.com/embed/9bv2Q4fQfp4"
          width='100%'
          height='100%'
        />
      </div>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
            <Feature />
        </div>
      </div>
    </section>
  );
}
