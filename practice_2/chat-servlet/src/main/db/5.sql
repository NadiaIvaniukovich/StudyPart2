SELECT * FROM users
WHERE id IN 
	(SELECT user_id FROM messages 
		GROUP BY user_id HAVING COUNT(user_id)>3
        )