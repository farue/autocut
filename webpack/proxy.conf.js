function setupProxy({ tls }) {
  const conf = [
    {
      // AC: Removed /services
      context: ['/api', '/management', '/v3/api-docs', '/h2-console', '/auth', '/health'],
      target: `http${tls ? 's' : ''}://localhost:8080`,
      secure: false,
      changeOrigin: tls,
    },
  ];
  return conf;
}

module.exports = setupProxy;
