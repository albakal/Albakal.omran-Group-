import React from 'react';

interface PlayLogoProps {
  className?: string;
  size?: number;
}

export const PlayLogo: React.FC<PlayLogoProps> = ({ className = '', size = 28 }) => {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 512 512"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={`shrink-0 ${className}`}
    >
      <defs>
        {/* Blue gradient for left body */}
        <linearGradient id="gplay_blue" x1="25" y1="25" x2="300" y2="300" gradientUnits="userSpaceOnUse">
          <stop stopColor="#00A0FF" />
          <stop offset="0.007" stopColor="#00A1FF" />
          <stop offset="0.26" stopColor="#00BEFF" />
          <stop offset="0.512" stopColor="#00D2FF" />
          <stop offset="0.76" stopColor="#00DFFF" />
          <stop offset="1" stopColor="#00E3FF" />
        </linearGradient>

        {/* Green gradient for top triangle */}
        <linearGradient id="gplay_green" x1="390" y1="120" x2="160" y2="350" gradientUnits="userSpaceOnUse">
          <stop stopColor="#00E676" />
          <stop offset="1" stopColor="#00C853" />
        </linearGradient>

        {/* Yellow gradient for right triangle */}
        <linearGradient id="gplay_yellow" x1="480" y1="256" x2="280" y2="256" gradientUnits="userSpaceOnUse">
          <stop stopColor="#FFD600" />
          <stop offset="1" stopColor="#FFAB00" />
        </linearGradient>

        {/* Red gradient for bottom triangle */}
        <linearGradient id="gplay_red" x1="160" y1="160" x2="390" y2="390" gradientUnits="userSpaceOnUse">
          <stop stopColor="#FF1744" />
          <stop offset="1" stopColor="#D50000" />
        </linearGradient>

        <filter id="shadow" x="-10%" y="-10%" width="120%" height="120%">
          <feDropShadow dx="0" dy="4" stdDeviation="6" floodColor="#000000" floodOpacity="0.2" />
        </filter>
      </defs>

      <g filter="url(#shadow)">
        {/* Left / Center Blue Polygon */}
        <path
          d="M58.2 38.6C52.6 44.5 49.3 53.6 49.3 65.2V446.8C49.3 458.4 52.6 467.5 58.2 473.4L60.5 475.6L290.4 245.7V240.3L60.5 10.4L58.2 38.6Z"
          fill="url(#gplay_blue)"
        />

        {/* Top Green Polygon */}
        <path
          d="M367.1 322.4L290.4 245.7L60.5 475.6C68.6 484.2 81.8 485.4 96.9 476.9L367.1 322.4Z"
          fill="url(#gplay_green)"
        />

        {/* Bottom Red Polygon */}
        <path
          d="M367.1 189.6L96.9 35.1C81.8 26.6 68.6 27.8 60.5 36.4L290.4 266.3L367.1 189.6Z"
          fill="url(#gplay_red)"
        />

        {/* Right Yellow Tip Polygon */}
        <path
          d="M367.1 322.4L444.6 278.2C460.6 269 460.6 243 444.6 233.8L367.1 189.6L290.4 256L367.1 322.4Z"
          fill="url(#gplay_yellow)"
        />

        {/* Subtle shadow overlay inside to give 3D depth */}
        <path
          d="M367.1 319.4L290.4 242.7L60.5 472.6C66.1 478.5 75.3 481.5 86.4 478L367.1 319.4Z"
          fill="#000000"
          fillOpacity="0.08"
        />
        <path
          d="M367.1 192.6L86.4 34C75.3 30.5 66.1 33.5 60.5 39.4L290.4 269.3L367.1 192.6Z"
          fill="#ffffff"
          fillOpacity="0.12"
        />
      </g>
    </svg>
  );
};
