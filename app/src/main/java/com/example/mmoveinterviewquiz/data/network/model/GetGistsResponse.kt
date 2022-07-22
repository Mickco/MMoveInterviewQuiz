package com.example.mmoveinterviewquiz.data.network.model


data class GetGistsResponseItem(
    val comments: Int,
    val comments_url: String,
    val commits_url: String,
    val created_at: String,
    val description: String,
    val forks_url: String,
    val git_pull_url: String,
    val git_push_url: String,
    val html_url: String,
    val id: String,
    val node_id: String,
    val owner: Owner,
    val `public`: Boolean,
    val truncated: Boolean,
    val updated_at: String,
    val url: String,
)


data class Owner(
    val avatar_url: String,
    val events_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val gravatar_id: String,
    val html_url: String,
    val id: Int,
    val login: String,
    val node_id: String,
    val organizations_url: String,
    val received_events_url: String,
    val repos_url: String,
    val site_admin: Boolean,
    val starred_url: String,
    val subscriptions_url: String,
    val type: String,
    val url: String
)
