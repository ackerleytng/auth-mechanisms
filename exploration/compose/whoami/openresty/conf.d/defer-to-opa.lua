local json = require "pretty.json"
local http = require "resty.http"
local Url = require "min.url"


-- splits a string s by /
-- "/" => {} (empty table)
-- "/api" => {"api"}
-- "/api/" => {"api"}
local function split(s)
  local out = {}
  for w in s:gmatch("([^/]+)") do
    table.insert(out, w)
  end
  return out
end


local httpc = http.new()

local path = Url.parse(ngx.var.uri).pathname
local path_split = split(path)

local auth_header = ngx.var.http_Authorization
if auth_header then
  _, _, token = string.find(auth_header, "Bearer%s+(.+)")
end

local request_specs = {
  method = ngx.req.get_method(),
  path = path_split,
  token = token,
}

local res, err = httpc:request_uri("http://whoami-opa:8181/v1/data/authz", {
  method = "POST",
  body = json.stringify({input = request_specs}),
})

if (err or res == nil) then
  ngx.status = ngx.HTTP_FORBIDDEN
  ngx.print(err)
  ngx.exit(ngx.HTTP_FORBIDDEN)
end

if res.status ~= 200 then
  ngx.status = ngx.HTTP_FORBIDDEN
  ngx.print(res.body)
  ngx.exit(ngx.HTTP_FORBIDDEN)
end

local r = json.parse(res.body)
if not (r.result and r.result.allow) then
  ngx.status = ngx.HTTP_FORBIDDEN
  ngx.print(res.body)
  ngx.exit(ngx.HTTP_FORBIDDEN)
end