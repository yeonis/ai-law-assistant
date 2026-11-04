#!/bin/bash

# LAW:ON AI 조문 재순위화 API 테스트

curl -sS -X POST "http://localhost:8080/api/ai/ask" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "협정관세 적용 조건이 궁금해요",
    "date": "2026-10-31"
  }' \
  | python3 -m json.tool --no-ensure-ascii



  #bash script/test-ai.sh