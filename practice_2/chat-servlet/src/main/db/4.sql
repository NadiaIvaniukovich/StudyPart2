SELECT * FROM messages WHERE (NOT LOCATE('hello',text)=0)
AND user_id=7