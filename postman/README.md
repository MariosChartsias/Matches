# Postman demo

The collection runs a complete authenticated API workflow: login, list, create,
read, update, validation, duplicate detection, and delete for matches and odds.

## Import into Postman

1. Click **Import** in Postman.
2. Select `Matches API.postman_collection.json` and
   `Matches API.postman_environment.json` from this directory.
3. Select the **Matches API - Local** environment.
4. Open **Matches API** and run any protected request, or click **Run** to run the
   collection in its numbered order.

The collection-level pre-request script automatically logs in before a protected
request when no token exists or the current token expires within 30 seconds. It
uses `authUsername` and `authPassword` (both default to `admin`), stores the token
as `jwtToken`, and the collection sends it as a Bearer token. **00 - Login** is
still available when you want to request a token manually.

If you imported the collection before JWT support was added, delete the old
collection and import these two files again. Postman does not watch local files
for updates.

The collection captures `matchId` and `oddId` after create requests. No IDs need to
be copied manually. It also cleans up the match and odd it creates.

The API must be running first:

```bash
docker compose up --build -d
```

Run the same collection outside the Postman UI with:

```bash
npx newman run "postman/Matches API.postman_collection.json" \
  -e "postman/Matches API.postman_environment.json"
```

