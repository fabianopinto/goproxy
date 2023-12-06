import { IncomingMessage, ServerResponse } from "http";
import { Proxy } from "./proxy";
import { parseRequest } from "./parser";

export function customListener(legacy: string, release: string) {
  const source = new Proxy(new URL(legacy));
  const target = new Proxy(new URL(release));

  return async (req: IncomingMessage, res: ServerResponse) => {
    const { body, group, status } = await parseRequest(req);
    const [leader, shadow] = status === "COMPLETE" ? [target, source] : [source, target];

    leader.modifyResponse = (res: ServerResponse) => {
      res.setHeader("X-goproxy", `group=${group};status=${status}`);
    };
    shadow.modifyResponse = undefined;

    await Promise.all([
      // leader.serveProxy(req, res, body),
      shadow.serveProxy(clone(req), res, body),
    ]);
  };

  function clone<T>(dolly: T): T {
    return { ...dolly } as T;
  }
}
