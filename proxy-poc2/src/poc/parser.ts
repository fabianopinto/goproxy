import { IncomingMessage } from "http";
import { createClient } from "redis";

export type ParseResult = {
  body: string,
  group: string,
  status: string,
}

const regexp1 = new RegExp("^/contact/(\\w+).*$");
const regexp2 = new RegExp("^.*\"group\":\\s*\"(\\w+)\".*$", "s");

const client = createClient({ url: "redis://localhost:6379" })
  .on("error", err => console.error(err))
  .connect();

export async function parseRequest(req: IncomingMessage): Promise<ParseResult> {
  let body = "";
  for await (const chunk of req) {
    body += chunk;
  }

  const group = regexp1.exec(req.url)?.at(1) ?? regexp2.exec(body)?.at(1);
  if (!group) {
    throw new Error("Failed to parse \"group\" property.");
  }

  const status = await (await client).get(group);
  if (!status) {
    console.error("Failed to fetch \"%s\" status", group);
  }

  return { body, group, status };
}
