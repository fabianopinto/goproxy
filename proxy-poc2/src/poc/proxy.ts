import { IncomingMessage, request, ServerResponse } from "http";

export class Proxy {
  constructor(private url: URL) {
  }

  modifyResponse: (res: ServerResponse) => void;

  async serveProxy(req: IncomingMessage, res: ServerResponse, body: string) {
    request(
      {
        method: req.method,
        protocol: this.url.protocol,
        host: this.url.hostname,
        port: this.url.port,
        path: req.url,
        headers: req.headers,
      }, (proxyRes) => {
        console.log(`response: ${proxyRes}`);
        res.statusCode = proxyRes.statusCode;
        for (let name in proxyRes.headers) {
          res.appendHeader(name, proxyRes.headers[name]);
        }
        if (this.modifyResponse) {
          this.modifyResponse(res);
        }
        proxyRes.pipe(res);
      })
      .end(body);
  }
}
