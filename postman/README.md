# Postman demo

The collection runs a complete API workflow: list, create, read, update, validation,
duplicate detection, and delete for matches and odds. It contains 13 requests and
26 automatic assertions.

## Import into Postman

1. Click **Import** in Postman.
2. Select `Matches API.postman_collection.json` and
   `Matches API.postman_environment.json` from this directory.
3. Select the **Matches API - Local** environment.
4. Open **Matches API**, click **Run**, and run the requests in their numbered order.

The collection captures `matchId` and `oddId` after create requests. No IDs need to
be copied manually. It also cleans up the match and odd it creates.

The API must be running first:

```bash
docker compose up -d
```

Run the same collection outside the Postman UI with:

```bash
npx newman run "postman/Matches API.postman_collection.json" \
  -e "postman/Matches API.postman_environment.json"
```

