import { createServer } from "http";
import { monitor } from "./poc/monitor";
import { customListener } from "./poc/handler";

// monitor();

createServer(customListener("http://localhost:8081", "http://localhost:8082"))
  .listen(3000);
